import axios from "axios";

const dictionaries = {
    state: {
        dictionaries: {
            all: {
                items: null
            },
            current: {
                item: null
            },
            display: null
        }
    },
    mutations: {
        set_dictionaries(state, payload) {
            state.dictionaries.all.items = payload.items
        },
        set_dictionary(state, payload) {
            state.dictionaries.current.item = payload.item
        },
        set_dictionary_display(state, payload) {
            state.dictionaries.display = payload.display
        }
    },
    actions: {
        findAllDictionaries({ commit }) {
            return new Promise((resolve, reject) => {
                axios
                    .get("/rest/dictionaries")
                    .then(response => {
                        commit("set_dictionaries", { items: response.data });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: "Couldn't retrieve dictionaries", err: error } });
                        commit("set_dictionaries", { items: [] });
                        reject(error);
                    })
            })
        },
        findDictionary({ commit }, id) {
            return new Promise((resolve, reject) => {
                commit("set_dictionary", { item: null });
                axios
                    .get(`/rest/dictionaries/${id}`)
                    .then(response => {
                        commit("set_dictionary", { item: response.data });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: `Couldn't retrieve dictionary with id ${id}`, err: error } });
                        commit("set_dictionary", { item: null });
                        reject(error);
                    })
            })
        },
        addDictionary({ commit }, dictionary) {
            return new Promise((resolve, reject) => {
                axios
                    .post('/rest/dictionaries', dictionary)
                    .then(response => {
                        commit("message_add", { message: { type: "info", title: "Add dictionary", text: `Dictionary ${response.data.name} added successful.` } });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: "An error occured while adding dictionary", err: error } });
                        reject(error);
                    })
            })
        },
        deleteDictionary({ commit }, dictionary) {
            return new Promise((resolve, reject) => {
                axios
                    .delete(`/rest/dictionaries/${dictionary.id}`)
                    .then(response => {
                        commit("message_add", { message: { type: "info", title: "Delete dictionary", text: `Dictionary ${response.data.name} deleted successful.` } });
                        commit("set_sut", { sut: null });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: `Couldn't delete dictionary with id ${dictionary.id}`, err: error } });
                        reject(error);
                    })
            })
        },
    },
    getters: {
        dictionaries: state => {
            return state.dictionaries
        },
        dictionariesForSelection: state => {
            let dictionariesForSelection = []

            if (state.dictionaries.all.items !== null) {
            	dictionariesForSelection = state.dictionaries.all.items.map(
                    dictionary => {
                        const newDictionary = {};
                        newDictionary["value"] = dictionary.id;
                        newDictionary["text"] = dictionary.name;
                        return newDictionary;
                    }
                );
            }
            return dictionariesForSelection;
        }
    }
}

export default dictionaries;