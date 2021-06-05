import axios from "axios";

const configurations = {
    state: {
        configurations: {
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
        set_configurations(state, payload) {
            state.configurations.all.items = payload.items
        },
        set_configuration(state, payload) {
            state.configurations.current.item = payload.item
        },
        set_configuration_display(state, payload) {
            state.configurations.display = payload.display
        }
    },
    actions: {
        findAllConfigurations({ commit }) {
            return new Promise((resolve, reject) => {
                axios
                    .get("/rest/configurations")
                    .then(response => {
                        commit("set_configurations", { items: response.data });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: "Couldn't retrieve configurations", err: error } });
                        commit("set_configurations", { items: [] });
                        reject(error);
                    })
            })
        },
        findConfiguration({ commit }, id) {
            return new Promise((resolve, reject) => {
                commit("set_configuration", { ìtem: null });
                axios
                    .get(`/rest/configurations/${id}`)
                    .then(response => {
                        commit("set_configuration", { item: response.data });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: `Couldn't retrieve configuration with id ${id}`, err: error } });
                        commit("set_configuration", { item: null });
                        reject(error);
                    })
            })
        },
        addConfiguration({ commit }, configuration) {
            return new Promise((resolve, reject) => {
                axios
                    .post('/rest/configurations', configuration)
                    .then(response => {
                        commit("message_add", { message: { type: "info", title: "Add configuration", text: `Configuration ${response.data.name} added successful.` } });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: "An error occured while adding configuration", err: error } });
                        reject(error);
                    })
            })
        },
        deleteConfiguration({ commit }, configuration) {
            return new Promise((resolve, reject) => {
                axios
                    .delete(`/rest/configurations/${configuration.id}`)
                    .then(response => {
                        commit("message_add", { message: { type: "info", title: "Delete configuations", text: `Configuration ${response.data.name} deleted successful.` } });
                        commit("set_configuration", { item: null });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: `Couldn't delete configuations with id ${configuations.id}`, err: error } });
                        reject(error);
                    })
            })
        },
    },
    getters: {
        configurations: state => {
            return state.configurations
        },
        configurationsForSelection: state => {
            let configurationsForSelection = []

            if (state.configurations.all.items !== null) {
                configurationsForSelection = state.configurations.all.items.map(
                    configuration => {
                        const newConfiguration = {};
                        newConfiguration["value"] = configuration.id;
                        newConfiguration["text"] = configuration.name;
                        return newConfiguration;
                    }
                );
            }

            return configurationsForSelection;
        }
    }
}

export default configurations;