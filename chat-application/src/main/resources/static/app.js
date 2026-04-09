const API_BASE = window.location.port === "5500" ? "http://localhost:8080" : window.location.origin;
const WS_BASE = API_BASE;

const emoji = (...codePoints) => String.fromCodePoint(...codePoints);

const REACTION_OPTIONS = [
    { key: "LIKE", icon: emoji(0x1F44D) },
    { key: "LOVE", icon: emoji(0x2764, 0xFE0F) },
    { key: "LAUGH", icon: emoji(0x1F602) },
    { key: "FIRE", icon: emoji(0x1F525) },
    { key: "WOW", icon: emoji(0x1F62E) }
];

const REACTION_MAP = Object.fromEntries(REACTION_OPTIONS.map(option => [option.key, option.icon]));

const state = {
    token: localStorage.getItem("pulsechat_token") || "",
    currentUser: JSON.parse(localStorage.getItem("pulsechat_user") || "null"),
    chats: [],
    users: [],
    notifications: [],
    activeChat: null,
    activeMessages: [],
    chatMode: "PRIVATE",
    stompClient: null,
    subscriptions: new Map(),
    typingUsers: new Map(),
    typingTimeout: null,
    searchTerm: "",
    activeMessageSearch: "",
    pinnedChatIds: []
};

const elements = {
    authSection: document.getElementById("authSection"),
    chatSection: document.getElementById("chatSection"),
    loginForm: document.getElementById("loginForm"),
    registerForm: document.getElementById("registerForm"),
    showLoginBtn: document.getElementById("showLoginBtn"),
    showRegisterBtn: document.getElementById("showRegisterBtn"),
    currentUserName: document.getElementById("currentUserName"),
    currentUserAvatar: document.getElementById("currentUserAvatar"),
    chatList: document.getElementById("chatList"),
    chatCount: document.getElementById("chatCount"),
    peopleList: document.getElementById("peopleList"),
    peopleCount: document.getElementById("peopleCount"),
    chatTitle: document.getElementById("chatTitle"),
    participantSummary: document.getElementById("participantSummary"),
    messageSearchInput: document.getElementById("messageSearchInput"),
    messageSearchMeta: document.getElementById("messageSearchMeta"),
    typingIndicator: document.getElementById("typingIndicator"),
    messages: document.getElementById("messages"),
    messageForm: document.getElementById("messageForm"),
    messageInput: document.getElementById("messageInput"),
    attachmentInput: document.getElementById("attachmentInput"),
    voiceInput: document.getElementById("voiceInput"),
    notificationList: document.getElementById("notificationList"),
    notificationCount: document.getElementById("notificationCount"),
    chatModal: document.getElementById("chatModal"),
    modalTitle: document.getElementById("modalTitle"),
    groupNameWrap: document.getElementById("groupNameWrap"),
    groupNameInput: document.getElementById("groupNameInput"),
    userPicker: document.getElementById("userPicker"),
    searchInput: document.getElementById("searchInput"),
    searchMeta: document.getElementById("searchMeta"),
    searchResults: document.getElementById("searchResults"),
    totalMessagesStat: document.getElementById("totalMessagesStat"),
    onlinePeopleStat: document.getElementById("onlinePeopleStat"),
    unreadStat: document.getElementById("unreadStat"),
    groupsStat: document.getElementById("groupsStat"),
    onlinePeopleList: document.getElementById("onlinePeopleList"),
    recentActivityList: document.getElementById("recentActivityList"),
    quickPrivateBtn: document.getElementById("quickPrivateBtn"),
    quickGroupBtn: document.getElementById("quickGroupBtn")
};

function init() {
    bindEvents();
    renderAuthMode("login");
    if (state.token && state.currentUser) {
        enterWorkspace().catch(showError);
    }
}

function bindEvents() {
    elements.showLoginBtn.addEventListener("click", () => renderAuthMode("login"));
    elements.showRegisterBtn.addEventListener("click", () => renderAuthMode("register"));
    elements.loginForm.addEventListener("submit", handleLogin);
    elements.registerForm.addEventListener("submit", handleRegister);
    document.getElementById("logoutBtn").addEventListener("click", logout);
    document.getElementById("newPrivateChatBtn").addEventListener("click", () => openChatModal("PRIVATE"));
    document.getElementById("newGroupChatBtn").addEventListener("click", () => openChatModal("GROUP"));
    document.getElementById("createChatBtn").addEventListener("click", () => createChat().catch(showError));
    elements.messageForm.addEventListener("submit", sendMessage);
    elements.messageInput.addEventListener("input", handleTypingInput);
    elements.messageSearchInput.addEventListener("input", handleMessageSearch);
    elements.voiceInput.addEventListener("change", sendVoiceNote);
    elements.searchInput.addEventListener("input", handleSearch);
    elements.quickPrivateBtn.addEventListener("click", () => openChatModal("PRIVATE"));
    elements.quickGroupBtn.addEventListener("click", () => openChatModal("GROUP"));
}

function renderAuthMode(mode) {
    const isLogin = mode === "login";
    elements.loginForm.classList.toggle("hidden", !isLogin);
    elements.registerForm.classList.toggle("hidden", isLogin);
    elements.showLoginBtn.classList.toggle("active", isLogin);
    elements.showRegisterBtn.classList.toggle("active", !isLogin);
}

async function handleLogin(event) {
    event.preventDefault();
    await authenticate("/api/auth/login", {
        email: document.getElementById("loginEmail").value.trim(),
        password: document.getElementById("loginPassword").value
    });
}

async function handleRegister(event) {
    event.preventDefault();
    await authenticate("/api/auth/register", {
        username: document.getElementById("registerUsername").value.trim(),
        displayName: document.getElementById("registerDisplayName").value.trim(),
        email: document.getElementById("registerEmail").value.trim(),
        password: document.getElementById("registerPassword").value
    });
}

async function authenticate(path, payload) {
    try {
        const data = await request(path, {
            method: "POST",
            body: JSON.stringify(payload)
        });
        state.token = data.token;
        state.currentUser = data;
        localStorage.setItem("pulsechat_token", state.token);
        localStorage.setItem("pulsechat_user", JSON.stringify(data));
        await enterWorkspace();
    } catch (error) {
        showError(error);
    }
}

async function enterWorkspace() {
    elements.authSection.classList.add("hidden");
    elements.chatSection.classList.remove("hidden");
    elements.currentUserName.textContent = state.currentUser.displayName;
    elements.currentUserAvatar.textContent = getInitials(state.currentUser.displayName);
    loadPinnedChats();
    await Promise.all([loadUsers(), loadChats(), loadNotifications()]);
    connectSocket();
}

async function request(path, options = {}) {
    const isFormData = options.body instanceof FormData;
    const headers = {
        ...(state.token ? { Authorization: `Bearer ${state.token}` } : {}),
        ...(options.headers || {})
    };

    if (!isFormData && !headers["Content-Type"]) {
        headers["Content-Type"] = "application/json";
    }

    let response;
    try {
        response = await fetch(`${API_BASE}${path}`, { ...options, headers });
    } catch (error) {
        throw new Error(`Cannot reach backend at ${API_BASE}. Start Spring Boot with .\\mvnw.cmd spring-boot:run`);
    }

    if (response.status === 204) {
        return null;
    }

    const text = await response.text();
    const contentType = response.headers.get("content-type") || "";
    const isJson = contentType.includes("application/json");
    const data = text ? (isJson ? JSON.parse(text) : text) : null;

    if (!response.ok) {
        if (data && typeof data === "object" && data.message) {
            throw new Error(data.message);
        }
        throw new Error(`Request failed (${response.status}). Make sure the Spring Boot app is running on http://localhost:8080`);
    }

    if (!isJson && text) {
        throw new Error("Unexpected non-JSON response from backend. Open the app from http://localhost:8080 after starting Spring Boot.");
    }

    return data;
}

async function loadUsers() {
    state.users = await request("/api/users");
    renderUserPicker();
    renderPeople();
    renderInsights();
}

async function loadChats() {
    state.chats = await request("/api/chats");
    sortChats();
    renderChats();
    subscribeToChatTopics();
    renderInsights();
    if (state.activeChat) {
        const refreshed = state.chats.find(chat => chat.id === state.activeChat.id);
        if (refreshed) {
            await selectChat(refreshed.id);
        }
    }
}

async function loadNotifications() {
    state.notifications = await request("/api/notifications");
    renderNotifications();
}

function handleSearch(event) {
    state.searchTerm = event.target.value.trim().toLowerCase();
    renderChats();
    renderPeople();
    renderInsights();
}

function handleMessageSearch(event) {
    state.activeMessageSearch = event.target.value.trim().toLowerCase();
    renderMessages();
}

function sortChats() {
    state.chats.sort((first, second) => {
        const secondPinned = isPinnedChat(second.id) ? 1 : 0;
        const firstPinned = isPinnedChat(first.id) ? 1 : 0;
        const pinDiff = secondPinned - firstPinned;
        if (pinDiff !== 0) {
            return pinDiff;
        }

        const unreadDiff = (second.unreadCount || 0) - (first.unreadCount || 0);
        if (unreadDiff !== 0) {
            return unreadDiff;
        }

        const secondTime = second.lastMessage?.createdAt || second.updatedAt || "";
        const firstTime = first.lastMessage?.createdAt || first.updatedAt || "";
        return new Date(secondTime).getTime() - new Date(firstTime).getTime();
    });
}

function filteredChats() {
    if (!state.searchTerm) return state.chats;
    return state.chats.filter(chat => {
        const text = `${chat.name} ${chat.lastMessage?.content || ""}`.toLowerCase();
        return text.includes(state.searchTerm);
    });
}

function filteredUsers() {
    if (!state.searchTerm) return state.users;
    return state.users.filter(user =>
        `${user.displayName} ${user.username}`.toLowerCase().includes(state.searchTerm)
    );
}

function renderChats() {
    const chats = filteredChats();
    elements.chatCount.textContent = chats.length;
    elements.chatList.innerHTML = "";
    if (!chats.length) {
        elements.chatList.innerHTML = '<div class="chat-item"><p>No conversations match your search yet.</p></div>';
        return;
    }

    chats.forEach(chat => {
        const item = document.createElement("div");
        item.className = `chat-item ${state.activeChat?.id === chat.id ? "active" : ""}`;
        item.innerHTML = `
            <button class="chat-open-btn" type="button">
                <div class="chat-item-main">
                    <div class="avatar avatar-medium">${getInitials(chat.name)}</div>
                    <div class="chat-item-copy">
                        <h4>${escapeHtml(chat.name)}</h4>
                        <p>${chat.lastMessage ? previewMessage(chat.lastMessage) : "No messages yet"}</p>
                        ${isPinnedChat(chat.id) ? '<span class="pinned-label">Pinned</span>' : ""}
                    </div>
                </div>
                <div class="chat-meta">
                    <span>${chat.lastMessage?.createdAt ? formatTime(chat.lastMessage.createdAt) : ""}</span>
                    ${chat.unreadCount ? `<span class="unread-badge">${chat.unreadCount}</span>` : ""}
                </div>
            </button>
            <button class="chat-pin-btn ${isPinnedChat(chat.id) ? "active" : ""}" type="button" title="${isPinnedChat(chat.id) ? "Unpin" : "Pin"}">
                ${isPinnedChat(chat.id) ? "★" : "☆"}
            </button>
        `;
        item.querySelector(".chat-open-btn").addEventListener("click", () => selectChat(chat.id).catch(showError));
        item.querySelector(".chat-pin-btn").addEventListener("click", () => togglePinnedChat(chat.id));
        elements.chatList.appendChild(item);
    });
}

function renderPeople() {
    const users = filteredUsers();
    elements.peopleCount.textContent = users.length;
    elements.peopleList.innerHTML = "";
    if (!users.length) {
        elements.peopleList.innerHTML = '<div class="person-item"><div class="person-item-info"><h4>No people found</h4><p>Try another search or create another account.</p></div></div>';
        return;
    }

    users.forEach(user => {
        const item = document.createElement("div");
        item.className = "person-item";
        item.innerHTML = `
            <div class="person-main">
                <div class="avatar avatar-small">${getInitials(user.displayName)}</div>
                <div class="person-item-info">
                    <h4>${escapeHtml(user.displayName)}</h4>
                    <p>@${escapeHtml(user.username)}${user.online ? " - online" : " - offline"}</p>
                </div>
            </div>
            <button class="person-action" type="button">Message</button>
        `;
        item.querySelector("button").addEventListener("click", () => startPrivateChat(user).catch(showError));
        elements.peopleList.appendChild(item);
    });
}

function renderInsights() {
    const totalMessages = state.chats.filter(chat => chat.lastMessage).length;
    const unread = state.chats.reduce((total, chat) => total + (chat.unreadCount || 0), 0);
    const onlineUsers = state.users.filter(user => user.online);
    const onlinePeople = onlineUsers.length;
    const groups = state.chats.filter(chat => chat.type === "GROUP").length;
    const chats = filteredChats();
    const users = filteredUsers();
    const recentChats = [...state.chats]
        .filter(chat => chat.lastMessage)
        .sort((first, second) => {
            const secondTime = second.lastMessage?.createdAt || second.updatedAt || "";
            const firstTime = first.lastMessage?.createdAt || first.updatedAt || "";
            return new Date(secondTime).getTime() - new Date(firstTime).getTime();
        })
        .slice(0, 4);

    elements.totalMessagesStat.textContent = totalMessages;
    elements.onlinePeopleStat.textContent = onlinePeople;
    elements.unreadStat.textContent = unread;
    elements.groupsStat.textContent = groups;

    if (!state.searchTerm) {
        elements.searchMeta.textContent = `Browse ${state.chats.length} conversations and ${state.users.length} people in your workspace.`;
        elements.searchResults.classList.add("hidden");
        elements.searchResults.innerHTML = "";
    } else {
        const resultCount = chats.length + users.length;
        elements.searchMeta.textContent = `${resultCount} match${resultCount === 1 ? "" : "es"} for "${state.searchTerm}".`;
        renderSearchResults(chats, users);
    }

    elements.onlinePeopleList.innerHTML = "";
    if (!onlineUsers.length) {
        elements.onlinePeopleList.innerHTML = '<div class="online-person empty-state-card"><div class="online-person-copy"><strong>No one online</strong><span>Recent teammates will appear here when they connect.</span></div></div>';
    } else {
        onlineUsers.slice(0, 5).forEach(user => {
            const item = document.createElement("div");
            item.className = "online-person";
            item.innerHTML = `
                <div class="avatar avatar-small">${getInitials(user.displayName)}</div>
                <div class="online-person-copy">
                    <strong>${escapeHtml(user.displayName)}</strong>
                    <span>@${escapeHtml(user.username)}</span>
                </div>
                <button class="online-person-action" type="button">Open</button>
            `;
            item.querySelector("button").addEventListener("click", () => startPrivateChat(user).catch(showError));
            elements.onlinePeopleList.appendChild(item);
        });
    }

    elements.recentActivityList.innerHTML = "";
    if (!recentChats.length) {
        elements.recentActivityList.innerHTML = '<div class="activity-item"><div class="activity-copy"><strong>No activity yet</strong><span>Send your first message to populate this space.</span></div></div>';
        return;
    }

    recentChats.forEach(chat => {
        const item = document.createElement("button");
        item.className = "activity-item";
        item.type = "button";
        item.innerHTML = `
            <div class="activity-copy">
                <strong>${escapeHtml(chat.name)}</strong>
                <span>${escapeHtml(previewMessage(chat.lastMessage))}</span>
            </div>
            <span class="muted">${formatTime(chat.lastMessage.createdAt)}</span>
        `;
        item.addEventListener("click", () => selectChat(chat.id).catch(showError));
        elements.recentActivityList.appendChild(item);
    });
}

function renderSearchResults(chats, users) {
    elements.searchResults.classList.remove("hidden");
    elements.searchResults.innerHTML = "";

    if (!chats.length && !users.length) {
        elements.searchResults.innerHTML = '<div class="activity-item"><div class="activity-copy"><strong>No results</strong><span>Try a different name, username, or message preview.</span></div></div>';
        return;
    }

    if (chats.length) {
        const title = document.createElement("p");
        title.className = "search-section-title";
        title.textContent = "Chats";
        elements.searchResults.appendChild(title);

        const list = document.createElement("div");
        list.className = "search-result-list";
        chats.slice(0, 4).forEach(chat => {
            const item = document.createElement("button");
            item.className = "search-result-item";
            item.type = "button";
            item.innerHTML = `
                <div class="avatar avatar-small">${getInitials(chat.name)}</div>
                <div class="search-result-copy">
                    <strong>${escapeHtml(chat.name)}</strong>
                    <span>${escapeHtml(chat.lastMessage ? previewMessage(chat.lastMessage) : "No messages yet")}</span>
                </div>
            `;
            item.addEventListener("click", () => selectChat(chat.id).catch(showError));
            list.appendChild(item);
        });
        elements.searchResults.appendChild(list);
    }

    if (users.length) {
        const title = document.createElement("p");
        title.className = "search-section-title";
        title.textContent = "People";
        elements.searchResults.appendChild(title);

        const list = document.createElement("div");
        list.className = "search-result-list";
        users.slice(0, 4).forEach(user => {
            const item = document.createElement("button");
            item.className = "search-result-item";
            item.type = "button";
            item.innerHTML = `
                <div class="avatar avatar-small">${getInitials(user.displayName)}</div>
                <div class="search-result-copy">
                    <strong>${escapeHtml(user.displayName)}</strong>
                    <span>@${escapeHtml(user.username)}${user.online ? " - online" : " - offline"}</span>
                </div>
            `;
            item.addEventListener("click", () => startPrivateChat(user).catch(showError));
            list.appendChild(item);
        });
        elements.searchResults.appendChild(list);
    }
}

async function startPrivateChat(user) {
    const existingChat = state.chats.find(chat =>
        chat.type === "PRIVATE" && chat.participants.some(participant => participant.userId === user.id)
    );

    if (existingChat) {
        await selectChat(existingChat.id);
        return;
    }

    const createdChat = await request("/api/chats", {
        method: "POST",
        body: JSON.stringify({
            name: null,
            type: "PRIVATE",
            participantIds: [user.id]
        })
    });

    await loadChats();
    await selectChat(createdChat.id);
}

async function selectChat(chatId) {
    state.activeChat = state.chats.find(chat => chat.id === chatId) || null;
    if (state.activeChat) {
        state.activeChat.unreadCount = 0;
    }
    state.activeMessageSearch = "";
    elements.messageSearchInput.value = "";
    renderChats();
    renderInsights();
    if (!state.activeChat) return;

    state.typingUsers.clear();
    renderTypingIndicator();
    elements.chatTitle.textContent = state.activeChat.name;
    elements.participantSummary.innerHTML = state.activeChat.participants
        .map(participant => `<span class="participant-tag">${escapeHtml(participant.displayName)}${participant.online ? " - online" : ""}</span>`)
        .join("");

    state.activeMessages = await request(`/api/chats/${chatId}/messages`);
    renderMessages();
    elements.messageForm.classList.remove("hidden");
}

function renderMessages() {
    elements.messages.classList.remove("empty-state");
    elements.messages.innerHTML = "";
    const visibleMessages = getVisibleMessages();

    if (!state.activeChat) {
        elements.messageSearchMeta.textContent = "Open a chat to search its messages.";
    } else if (!state.activeMessageSearch) {
        elements.messageSearchMeta.textContent = `Showing ${state.activeMessages.length} message${state.activeMessages.length === 1 ? "" : "s"} in this chat.`;
    } else {
        elements.messageSearchMeta.textContent = `${visibleMessages.length} match${visibleMessages.length === 1 ? "" : "es"} in this chat.`;
    }

    if (!visibleMessages.length) {
        elements.messages.classList.add("empty-state");
        elements.messages.innerHTML = state.activeMessageSearch
            ? "<div><h3>No messages found</h3><p>Try a different word from this conversation.</p></div>"
            : "<div><h3>Start the conversation</h3><p>Type a message below and it will be delivered to the selected person or group.</p></div>";
        return;
    }

    visibleMessages.forEach(message => {
        const row = document.createElement("div");
        row.className = `message-row ${message.senderId === state.currentUser.userId ? "me" : ""}`;
        row.innerHTML = `
            <div class="message-bubble">
                <div class="message-meta">
                    <span>${escapeHtml(message.senderName)}</span>
                    <span class="message-meta-right">
                        <span>${formatTime(message.createdAt)}</span>
                        ${renderMessageStatus(message)}
                    </span>
                </div>
                ${message.content ? `<p class="message-body-text">${escapeHtml(message.content)}</p>` : ""}
                ${renderAttachment(message)}
                ${renderFlags(message)}
                ${renderReactionRow(message)}
                ${renderMessageActions(message)}
                ${renderReceiptSummary(message)}
            </div>
        `;
        wireMessageActions(row, message);
        elements.messages.appendChild(row);
    });
    elements.messages.scrollTop = elements.messages.scrollHeight;
}

function renderAttachment(message) {
    if (!message.attachmentUrl) return "";
    const attachmentUrl = `${API_BASE}${message.attachmentUrl}`;
    if (message.voiceNote) {
        return `<audio class="audio-note" controls src="${attachmentUrl}"></audio>`;
    }
    return `<a class="message-attachment" href="${attachmentUrl}" target="_blank" rel="noreferrer">${escapeHtml(message.attachmentName || "Attachment")}</a>`;
}

function renderFlags(message) {
    const flags = [];
    if (message.edited) flags.push("edited");
    if (message.deleted) flags.push("deleted");
    if (message.voiceNote) flags.push("voice note");
    return flags.length ? `<div class="message-flags">${flags.join(" - ")}</div>` : "";
}

function renderReactionRow(message) {
    const grouped = groupReactions(message.reactions || []);
    const chips = Object.entries(grouped)
        .map(([key, list]) => `<button class="reaction-chip" data-reaction="${key}">${reactionIcon(key)} ${list.length}</button>`)
        .join("");
    const picker = REACTION_OPTIONS
        .map(option => `<button class="reaction-chip" data-add-reaction="${option.key}">${option.icon}</button>`)
        .join("");
    return `<div class="reaction-row">${chips}${picker}</div>`;
}

function renderMessageActions(message) {
    if (message.deleted) return "";
    const own = message.senderId === state.currentUser.userId;
    return `<div class="message-actions">${own ? `<button type="button" data-edit-message="${message.id}">Edit</button><button type="button" data-delete-message="${message.id}">Delete</button>` : ""}</div>`;
}

function wireMessageActions(container, message) {
    container.querySelectorAll("[data-add-reaction]").forEach(button => {
        button.addEventListener("click", () => toggleReaction(message.id, button.dataset.addReaction).catch(showError));
    });
    container.querySelectorAll("[data-reaction]").forEach(button => {
        button.addEventListener("click", () => toggleReaction(message.id, button.dataset.reaction).catch(showError));
    });
    const editButton = container.querySelector("[data-edit-message]");
    if (editButton) {
        editButton.addEventListener("click", () => editMessage(message).catch(showError));
    }
    const deleteButton = container.querySelector("[data-delete-message]");
    if (deleteButton) {
        deleteButton.addEventListener("click", () => deleteMessage(message.id).catch(showError));
    }
}

async function sendMessage(event) {
    event.preventDefault();
    if (!state.activeChat) return;

    const content = elements.messageInput.value.trim();
    const file = elements.attachmentInput.files[0];
    if (!content && !file) return;

    try {
        let createdMessage;
        if (file) {
            const formData = new FormData();
            if (content) formData.append("content", content);
            formData.append("file", file);
            createdMessage = await request(`/api/chats/${state.activeChat.id}/attachments`, { method: "POST", body: formData });
        } else if (state.stompClient?.connected) {
            state.stompClient.publish({
                destination: "/app/chat.send",
                headers: { Authorization: `Bearer ${state.token}` },
                body: JSON.stringify({ chatRoomId: state.activeChat.id, content })
            });
        } else {
            createdMessage = await request(`/api/chats/${state.activeChat.id}/messages`, {
                method: "POST",
                body: JSON.stringify({ content })
            });
        }

        if (createdMessage) upsertIncomingMessage(createdMessage);
        resetComposer();
        await loadChats();
    } catch (error) {
        showError(error);
    }
}

async function sendVoiceNote() {
    if (!state.activeChat) return;
    const file = elements.voiceInput.files[0];
    if (!file) return;

    try {
        const formData = new FormData();
        formData.append("file", file);
        const createdMessage = await request(`/api/chats/${state.activeChat.id}/voice-notes`, { method: "POST", body: formData });
        upsertIncomingMessage(createdMessage);
        elements.voiceInput.value = "";
        await loadChats();
    } catch (error) {
        showError(error);
    }
}

function connectSocket() {
    if (state.stompClient?.connected) return;

    const socketUrl = `${WS_BASE.replace(/\/$/, "")}/ws-chat`;
    const client = new StompJs.Client({
        webSocketFactory: () => new SockJS(socketUrl),
        connectHeaders: { Authorization: `Bearer ${state.token}` },
        debug: () => {},
        reconnectDelay: 5000,
        onConnect: () => {
            state.stompClient = client;
            subscribeToChatTopics();
            subscribeToNotifications();
        },
        onWebSocketError: () => showError(new Error(`WebSocket could not connect to ${WS_BASE}. Start Spring Boot first.`)),
        onStompError: frame => console.error(frame)
    });

    client.activate();
    state.stompClient = client;
}

function subscribeToChatTopics() {
    if (!state.stompClient?.connected) return;

    state.chats.forEach(chat => {
        subscribeIfNeeded(`chat-${chat.id}`, `/topic/chat.${chat.id}`, message => {
            upsertIncomingMessage(JSON.parse(message.body));
            renderChats();
            renderInsights();
        });
        subscribeIfNeeded(`refresh-${chat.id}`, `/topic/chat.refresh.${chat.id}`, message => {
            if (state.activeChat?.id === chat.id) {
                state.activeMessages = JSON.parse(message.body);
                renderMessages();
            }
        });
        subscribeIfNeeded(`receipts-${chat.id}`, `/topic/chat.receipts.${chat.id}`, message => {
            if (state.activeChat?.id === chat.id) {
                state.activeMessages = JSON.parse(message.body);
                renderMessages();
            }
        });
        subscribeIfNeeded(`typing-${chat.id}`, `/topic/chat.typing.${chat.id}`, message => {
            const payload = JSON.parse(message.body);
            if (payload.userId === state.currentUser.userId || state.activeChat?.id !== payload.chatRoomId) return;
            if (payload.typing) state.typingUsers.set(payload.userId, payload.displayName);
            else state.typingUsers.delete(payload.userId);
            renderTypingIndicator();
        });
    });
}

function subscribeIfNeeded(key, destination, handler) {
    if (state.subscriptions.has(key)) return;
    state.subscriptions.set(key, state.stompClient.subscribe(destination, handler));
}

function subscribeToNotifications() {
    subscribeIfNeeded("notifications", "/user/queue/notifications", message => {
        state.notifications.unshift(JSON.parse(message.body));
        renderNotifications();
    });
}

function upsertIncomingMessage(message) {
    const chatIndex = state.chats.findIndex(item => item.id === message.chatRoomId);
    if (chatIndex >= 0) {
        state.chats[chatIndex].lastMessage = message;
        if (message.senderId !== state.currentUser.userId && state.activeChat?.id !== message.chatRoomId) {
            state.chats[chatIndex].unreadCount = (state.chats[chatIndex].unreadCount || 0) + 1;
        }
        sortChats();
    }
    if (state.activeChat?.id === message.chatRoomId) {
        const idx = state.activeMessages.findIndex(item => item.id === message.id);
        if (idx >= 0) state.activeMessages[idx] = message;
        else state.activeMessages.push(message);
        renderMessages();
    }
}

async function toggleReaction(messageId, emojiKey) {
    const updated = await request(`/api/chats/messages/${messageId}/reactions`, {
        method: "POST",
        body: JSON.stringify({ emoji: emojiKey })
    });
    upsertIncomingMessage(updated);
}

async function editMessage(message) {
    const content = prompt("Edit your message", message.content || "");
    if (!content || content.trim() === message.content) return;
    const updated = await request(`/api/chats/messages/${message.id}`, {
        method: "PATCH",
        body: JSON.stringify({ content: content.trim() })
    });
    upsertIncomingMessage(updated);
}

async function deleteMessage(messageId) {
    if (!confirm("Delete this message?")) return;
    const updated = await request(`/api/chats/messages/${messageId}`, { method: "DELETE" });
    upsertIncomingMessage(updated);
}

function renderNotifications() {
    elements.notificationCount.textContent = state.notifications.length;
    elements.notificationList.innerHTML = state.notifications.length ? "" : '<div class="notification-item"><p>No notifications yet.</p></div>';

    state.notifications.forEach(notification => {
        const item = document.createElement("button");
        item.className = `notification-item ${notification.read ? "" : "unread"}`;
        item.innerHTML = `<h4>${escapeHtml(notification.title)}</h4><p>${escapeHtml(notification.content)}</p><small class="muted">${formatTime(notification.createdAt)}</small>`;
        item.addEventListener("click", async () => {
            try {
                if (!notification.read) {
                    await request(`/api/notifications/${notification.id}/read`, { method: "PATCH" });
                    notification.read = true;
                    renderNotifications();
                }
            } catch (error) {
                showError(error);
            }
        });
        elements.notificationList.appendChild(item);
    });
}

function openChatModal(mode) {
    state.chatMode = mode;
    elements.modalTitle.textContent = mode === "GROUP" ? "New Group Chat" : "New Private Chat";
    elements.groupNameWrap.classList.toggle("hidden", mode !== "GROUP");
    elements.groupNameInput.value = "";
    renderUserPicker();
    elements.chatModal.showModal();
}

function renderUserPicker() {
    elements.userPicker.innerHTML = "";
    state.users.forEach(user => {
        const wrapper = document.createElement("div");
        wrapper.className = "user-option";
        wrapper.innerHTML = `<label><input type="checkbox" value="${user.id}"><span>${escapeHtml(user.displayName)} <small class="muted">@${escapeHtml(user.username)}${user.online ? " - online" : ""}</small></span></label>`;
        elements.userPicker.appendChild(wrapper);
    });
}

async function createChat() {
    const selectedUsers = [...elements.userPicker.querySelectorAll("input:checked")].map(input => input.value);
    if (!selectedUsers.length) {
        throw new Error("Select at least one user.");
    }

    const chat = await request("/api/chats", {
        method: "POST",
        body: JSON.stringify({
            name: state.chatMode === "GROUP" ? elements.groupNameInput.value.trim() : null,
            type: state.chatMode,
            participantIds: selectedUsers
        })
    });
    elements.chatModal.close();
    await loadChats();
    await selectChat(chat.id);
}

function handleTypingInput() {
    if (!state.activeChat || !state.stompClient?.connected) return;
    publishTyping(true);
    clearTimeout(state.typingTimeout);
    state.typingTimeout = setTimeout(() => stopTyping(), 1200);
}

function publishTyping(isTyping) {
    state.stompClient.publish({
        destination: "/app/chat.typing",
        headers: { Authorization: `Bearer ${state.token}` },
        body: JSON.stringify({ chatRoomId: state.activeChat.id, typing: isTyping })
    });
}

function stopTyping() {
    clearTimeout(state.typingTimeout);
    state.typingTimeout = null;
    if (state.stompClient?.connected && state.activeChat) {
        publishTyping(false);
    }
}

function renderTypingIndicator() {
    const names = [...state.typingUsers.values()];
    elements.typingIndicator.classList.toggle("hidden", !names.length);
    elements.typingIndicator.textContent = names.length ? `${names.join(", ")} ${names.length === 1 ? "is" : "are"} typing...` : "";
}

function renderReceiptSummary(message) {
    if (message.senderId !== state.currentUser.userId || !message.readReceipts?.length) return "";
    return `<div class="message-receipts">Seen by ${message.readReceipts.map(receipt => escapeHtml(receipt.displayName)).join(", ")}</div>`;
}

function renderMessageStatus(message) {
    if (message.senderId !== state.currentUser.userId) return "";

    if (message.status === "READ") {
        return '<span class="message-status read" title="Read">✓✓</span>';
    }
    if (message.status === "DELIVERED") {
        return '<span class="message-status delivered" title="Delivered">✓✓</span>';
    }
    return '<span class="message-status sent" title="Sent">✓</span>';
}

function getVisibleMessages() {
    if (!state.activeMessageSearch) {
        return state.activeMessages;
    }

    return state.activeMessages.filter(message => {
        const text = [
            message.senderName,
            message.content || "",
            message.attachmentName || "",
            ...(message.reactions || []).map(reaction => reaction.emoji)
        ].join(" ").toLowerCase();
        return text.includes(state.activeMessageSearch);
    });
}

function groupReactions(reactions) {
    return reactions.reduce((acc, reaction) => {
        acc[reaction.emoji] = acc[reaction.emoji] || [];
        acc[reaction.emoji].push(reaction);
        return acc;
    }, {});
}

function reactionIcon(key) {
    return REACTION_MAP[key] || key;
}

function previewMessage(message) {
    if (message.deleted) return "Message deleted";
    if (message.voiceNote) return "Voice note";
    if (message.attachmentName) return `Attachment: ${message.attachmentName}`;
    return message.content || "No messages yet";
}

function getInitials(name) {
    return String(name || "?")
        .trim()
        .split(/\s+/)
        .slice(0, 2)
        .map(part => part[0]?.toUpperCase() || "")
        .join("") || "?";
}

function getPinnedStorageKey() {
    return `pulsechat_pins_${state.currentUser?.userId || "guest"}`;
}

function loadPinnedChats() {
    try {
        state.pinnedChatIds = JSON.parse(localStorage.getItem(getPinnedStorageKey()) || "[]");
    } catch (error) {
        state.pinnedChatIds = [];
    }
}

function persistPinnedChats() {
    localStorage.setItem(getPinnedStorageKey(), JSON.stringify(state.pinnedChatIds));
}

function isPinnedChat(chatId) {
    return state.pinnedChatIds.includes(chatId);
}

function togglePinnedChat(chatId) {
    if (isPinnedChat(chatId)) {
        state.pinnedChatIds = state.pinnedChatIds.filter(id => id !== chatId);
    } else {
        state.pinnedChatIds = [chatId, ...state.pinnedChatIds];
    }
    persistPinnedChats();
    sortChats();
    renderChats();
}

function resetComposer() {
    stopTyping();
    elements.messageInput.value = "";
    elements.attachmentInput.value = "";
    elements.voiceInput.value = "";
}

function logout() {
    state.subscriptions.forEach(subscription => subscription.unsubscribe());
    state.subscriptions.clear();
    state.stompClient?.deactivate();
    localStorage.removeItem("pulsechat_token");
    localStorage.removeItem("pulsechat_user");
    location.reload();
}

function formatTime(value) {
    return new Date(value).toLocaleString([], {
        month: "short",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit"
    });
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function showError(error) {
    alert(error.message || String(error));
}

init();
