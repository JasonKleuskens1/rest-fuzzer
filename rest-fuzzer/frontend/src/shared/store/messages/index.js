const messages = {
    state: {
        messages: []
    },
    mutations: {
        message_add(state, payload) {
            state.messages.push(payload.message);
        },
        messages_clear(state, payload) {
            state.messages = [];
        }        
    },
    actions: { },
    getters: {
        messages: state => {
            return state.messages
        }
    }
}

export default messages;