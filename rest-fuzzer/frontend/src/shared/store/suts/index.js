import axios from "axios";

function getCountActions({ commit }, data) {
    return new Promise((resolve, reject) => {
        let queryParams = '';
        if (data.context && data.context.filter !== null) { queryParams += `?filter=${data.context.filter}`; }
        axios
            .get(`/rest/suts/${data.sut_id}/actions/count${queryParams}`)
            .then(response => {
                resolve(response.data);
            })
            .catch(error => {
                commit("message_add", { message: { type: "error", text: `Couldn't retrieve sut action count for sut with id ${data.sut_id}`, err: error } });
                reject(error);
            })
    });
}

function getCountParameters({ commit }, data) {
    return new Promise((resolve, reject) => {
        let queryParams = '';
        if (data.context && data.context.filter !== null) { queryParams += `?filter=${data.context.filter}`; }
        axios
            .get(`/rest/suts/${data.sut_id}/parameters/count${queryParams}`)
            .then(response => {
                resolve(response.data);
            })
            .catch(error => {
                commit("message_add", { message: { type: "error", text: `Couldn't retrieve sut parameter count for sut with id ${data.sut_id}`, err: error } });
                reject(error);
            })
    });
}

function getCountActionsDependencies({ commit }, data) {
    return new Promise((resolve, reject) => {
        let queryParams = '';
        if (data.context && data.context.filter !== null) { queryParams += `?filter=${data.context.filter}`; }
        axios
            .get(`/rest/suts/${data.sut_id}/actions/dependencies/count${queryParams}`)
            .then(response => {
                resolve(response.data);
            })
            .catch(error => {
                commit("message_add", { message: { type: "error", text: `Couldn't retrieve sut action dependencies count for sut with id ${data.sut_id}`, err: error } });
                reject(error);
            })
    });
}

const suts = {
    state: {
        suts: {
            all: {
                items: null
            },
            current: {
                item: null,
                queued_or_running_tasks_count: null,
                actions: {
                    items: null,
                    total: null,
                    count: null
                },
                parameters: {
                    items: null,
                    total: null,
                    count: null
                },
                actions_dependencies: {
                    items: null,
                    total: null,
                    count: null
                },
                selection: {
                    actions: null,
                    parameters: null,
                    parameters_depends_on: null
                }
            },
            display: null
        }
    },
    mutations: {
        set_suts(state, payload) {
            state.suts.all.items = payload.items
        },
        set_sut(state, payload) {
            state.suts.current.item = payload.item
        },

        set_sut_running_or_queued_tasks_count(state, payload) {
            state.suts.current.queued_or_running_tasks_count = payload.count
        },

        set_sut_actions(state, payload) {
            state.suts.current.actions.items = payload.items
        },
        set_sut_actions_total(state, payload) {
            state.suts.current.actions.total = payload.total
        },
        set_sut_actions_count(state, payload) {
            state.suts.current.actions.count = payload.count
        },

        set_sut_parameters(state, payload) {
            state.suts.current.parameters.items = payload.items
        },
        set_sut_parameters_total(state, payload) {
            state.suts.current.parameters.total = payload.total
        },
        set_sut_parameters_count(state, payload) {
            state.suts.current.parameters.count = payload.count
        },

        set_sut_actions_dependencies(state, payload) {
            state.suts.current.actions_dependencies.items = payload.items
        },
        set_sut_actions_dependencies_total(state, payload) {
            state.suts.current.actions_dependencies.total = payload.total
        },
        set_sut_actions_dependencies_count(state, payload) {
            state.suts.current.actions_dependencies.count = payload.count
        },

        set_sut_selection_actions(state, payload) {
            state.suts.current.selection.actions = payload.actions
        },
        set_sut_selection_parameters(state, payload) {
            state.suts.current.selection.parameters = payload.parameters
        },
        set_sut_selection_parameters_depends_on(state, payload) {
            state.suts.current.selection.parameters_depends_on = payload.parameters
        },

        set_sut_display(state, payload) {
            state.suts.display = payload.display
        }
    },
    actions: {
        findAllSuts({ commit }) {
            return new Promise((resolve, reject) => {
                axios
                    .get("/rest/suts")
                    .then(response => {
                        commit("set_suts", { items: response.data });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: "Couldn't retrieve suts", err: error } });
                        commit("set_suts", { items: [] });
                        reject(error);
                    })
            })
        },
        findSut({ commit, dispatch }, id) {
            return new Promise((resolve, reject) => {
                axios
                    .get(`/rest/suts/${id}`)
                    .then(response => {
                        commit("set_sut", { item: response.data });
                        dispatch("countAllSutActions", { sut_id: id });
                        dispatch("countAllSutParameters", { sut_id: id });
                        dispatch("countAllSutActionsDependencies", { sut_id: id });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: `Couldn't retrieve sut with id ${id}`, err: error } });
                        commit("set_sut", { item: null });
                        reject(error);
                    })
            })
        },
        countSutRunningOrQueuedTasks({ commit }, id) {
            return new Promise((resolve, reject) => {
                axios
                    .get(`/rest/tasks/running_or_queued/suts/${id}/count`)
                    .then(response => {
                        commit("set_sut_running_or_queued_tasks_count", { count: response.data });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: `Couldn't retrieve tasks count (running or queued) for sut with id ${id}`, err: error } });
                        commit("set_sut_running_or_queued_tasks_count", { count: null });
                        reject(error);
                    })
            })
        },
        findSutActions({ commit, dispatch }, data) {
            return new Promise((resolve, reject) => {
                let queryParams = `?curPage=${data.context.currentPage}&perPage=${data.context.perPage}`;
                if (data.context.filter !== null) { queryParams += `&filter=${data.context.filter}`; }
                axios
                    .get(`/rest/suts/${data.sut_id}/actions/paginated/${queryParams}`)
                    .then(response => {
                        commit("set_sut_actions", { items: response.data });
                        dispatch("countSutActions", data);
                        dispatch("countSutActionsDependencies", data);
                        resolve();
                    })
                    .catch(error => {
                        commit("set_sut_actions", { items: null });
                        commit("message_add", { message: { type: "error", text: `Couldn't retrieve sut actions for sut with id ${data.sut_id}`, err: error } });
                        reject(error);
                    })
            })
        },
        countAllSutActions({ commit }, data) {
            getCountActions({ commit }, data)
                .then(total => {
                    commit("set_sut_actions_total", { total: total });
                });
        },
        countSutActions({ commit }, data) {
            getCountActions({ commit }, data)
                .then(count => {
                    commit("set_sut_actions_count", { count: count });
                });
        },
        findSutParameters({ commit, dispatch }, data) {
            return new Promise((resolve, reject) => {
                let queryParams = `?curPage=${data.context.currentPage}&perPage=${data.context.perPage}`;
                if (data.context.filter !== null) { queryParams += `&filter=${data.context.filter}`; }
                axios
                    .get(`/rest/suts/${data.sut_id}/parameters/paginated/${queryParams}`)
                    .then(response => {
                        commit("set_sut_parameters", { items: response.data });
                        dispatch("countSutParameters", data);
                        resolve();
                    })
                    .catch(error => {
                        commit("set_sut_parameters", { items: null });
                        commit("message_add", { message: { type: "error", text: `Couldn't retrieve sut parameters for sut with id ${data.sut_id}`, err: error } });
                        reject(error);
                    })
            })
        },
        countAllSutParameters({ commit }, data) {
            getCountParameters({ commit }, data)
                .then(total => {
                    commit("set_sut_parameters_total", { total: total });
                });
        },
        countSutParameters({ commit }, data) {
            getCountParameters({ commit }, data)
                .then(count => {
                    commit("set_sut_parameters_count", { count: count });
                });
        },
        findSutActionsDependencies({ commit, dispatch }, data) {
            return new Promise((resolve, reject) => {
                let queryParams = `?curPage=${data.context.currentPage}&perPage=${data.context.perPage}`;
                if (data.context.filter !== null) { queryParams += `&filter=${data.context.filter}`; }
                axios
                    .get(`/rest/suts/${data.sut_id}/actions/dependencies/paginated/${queryParams}`)
                    .then(response => {
                        commit("set_sut_actions_dependencies", { items: response.data });
                        dispatch("countSutActionsDependencies", data);
                        resolve();
                    })
                    .catch(error => {
                        commit("set_sut_actions_dependencies", { items: null });
                        commit("message_add", { message: { type: "error", text: `Couldn't retrieve sut action dependencies for sut with id ${data.sut_id}`, err: error } });
                        reject(error);
                    })
            })
        },
        countAllSutActionsDependencies({ commit }, data) {
            getCountActionsDependencies({ commit }, data)
                .then(total => {
                    commit("set_sut_actions_dependencies_total", { total: total });
                });
        },
        countSutActionsDependencies({ commit }, data) {
            getCountActionsDependencies({ commit }, data)
                .then(count => {
                    commit("set_sut_actions_dependencies_count", { count: count });
                });
        },
        findSelectionActions({ commit }, data) {
            return new Promise((resolve, reject) => {
                axios
                    .get(`/rest/suts/${data.sut_id}/actions`)
                    .then(response => {
                        commit("set_sut_selection_actions", { actions: response.data });
                        resolve();
                    })
                    .catch(error => {
                        commit("set_sut_selection_actions", { actions: null });
                        commit("message_add", { message: { type: "error", text: `Couldn't retrieve sut actions (selection) for sut with id ${data.sut_id}`, err: error } });
                        reject(error);
                    })
            })
        },
        findSelectionParameters({ commit }, data) {
            return new Promise((resolve, reject) => {
                axios
                    .get(`/rest/suts/${data.sut_id}/actions/${data.action_id}/parameters`)
                    .then(response => {
                        commit("set_sut_selection_parameters", { parameters: response.data });
                        resolve();
                    })
                    .catch(error => {
                        commit("set_sut_selection_parameters", { parameters: null });
                        commit("message_add", { message: { type: "error", text: `Couldn't retrieve parameters for action with id ${data.action_id} and sut with id ${data.sut_id}`, err: error } });
                        reject(error);
                    })
            })
        },
        findSelectionParametersDependsOn({ commit }, data) {
            return new Promise((resolve, reject) => {
                axios
                    .get(`/rest/suts/${data.sut_id}/actions/${data.action_id}/parameters`)
                    .then(response => {
                        commit("set_sut_selection_parameters_depends_on", { parameters: response.data });
                        resolve();
                    })
                    .catch(error => {
                        commit("set_sut_selection_parameters_depends_on", { parameters: null });
                        commit("message_add", { message: { type: "error", text: `Couldn't retrieve parameters for action with id ${data.action_id} and sut with id ${data.sut_id}`, err: error } });
                        reject(error);
                    })
            })
        },        
        addSut({ commit }, sut) {
            return new Promise((resolve, reject) => {
                axios
                    .post('/rest/suts', sut)
                    .then(response => {
                        commit("message_add", { message: { type: "info", title: "Add sut", text: `Sut ${response.data.location} added successful.` } });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: "An error occured while adding sut", err: error } });
                        reject(error);
                    })
            })
        },
        deleteSut({ commit }, sut) {
            return new Promise((resolve, reject) => {
                axios
                    .delete(`/rest/suts/${sut.id}`)
                    .then(response => {
                        commit("message_add", { message: { type: "info", title: "Delete system under test", text: `System under test ${response.data.location} deleted successful.` } });
                        commit("set_sut", { item: null });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: `Couldn't delete system under test with id ${sut.id}`, err: error } });
                        reject(error);
                    })
            })
        },
        addSutActionDependency({ commit, dispatch }, data) {
            return new Promise((resolve, reject) => {
                axios
                    .post(`/rest/suts/${data.sut_id}/actions/dependencies`, data.dependency)
                    .then(response => {
                        commit("message_add", { message: { type: "info", title: "Add dependency", text: `Dependency for action ${response.data.action.path} [${response.data.action.httpMethod}] added successful.` } });
                        dispatch("countAllSutActionsDependencies", { sut_id: data.sut_id });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: `Couldn't add dependency for action ${data.dependency.action}.`, err: error } });
                        reject(error);
                    })
            })
        },
        deleteSutActionDependency({ commit, dispatch }, data) {
            return new Promise((resolve, reject) => {
                axios
                    .delete(`/rest/suts/${data.sut_id}/actions/dependencies/${data.dependency.id}`)
                    .then(response => {
                        commit("message_add", { message: { type: "info", title: "Add action dependency", text: `Action dependency for action ${response.data.action.path} [${response.data.action.httpMethod}] deleted successful.` } });
                        dispatch("countAllSutActionsDependencies", { sut_id: data.sut_id });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: `Couldn't delete dependency for action ${data.dependency.action.path} [${data.dependency.action.httpMethod}].`, err: error } });
                        reject(error);
                    })
            })
        }        
    },
    getters: {
        suts: state => {
            return state.suts
        },
        sutsForSelection: state => {
            let sutsForSelection = []

            if (state.suts.all.items !== null) {
                sutsForSelection = state.suts.all.items.filter(sut => sut.title !== null).map(
                    sut => {
                        const newSut = {};
                        newSut["value"] = sut.id;
                        newSut["text"] = `${sut.title} (${sut.location})`;
                        return newSut;
                    }
                );
            }

            return sutsForSelection;
        },
        selectionActions: state => {
            let actions = [];

            if (state.suts.current.selection.actions !== null) {
                actions = state.suts.current.selection.actions.map(
                    action => {
                        const newAction = {};
                        newAction["value"] = action.id;
                        newAction["text"] = `${action.path} (${action.httpMethod})`;
                        return newAction;
                    }
                );
            }

            return actions;
        },
        selectionActionsPosts: state => {
            let actions = [];

            if (state.suts.current.selection.actions !== null) {
                actions = state.suts.current.selection.actions.filter(action => action.httpMethod === "POST").map(
                    action => {
                        const newAction = {};
                        newAction["value"] = action.id;
                        newAction["text"] = `${action.path} (${action.httpMethod})`;
                        return newAction;
                    }
                );
            }

            return actions;
        },
        selectionParameters: state => {
            let parameters = [];

            if (state.suts.current.selection.parameters !== null) {
                parameters = state.suts.current.selection.parameters.map(
                    parameter => {
                        const newParameter = {};
                        newParameter["value"] = parameter.id;
                        newParameter["text"] = `${parameter.name} (${parameter.type})`;
                        return newParameter;
                    }
                );
            }

            return parameters;
        },
        selectionParametersDependsOn: state => {
            let parameters = [];

            if (state.suts.current.selection.parameters_depends_on !== null) {
                parameters = state.suts.current.selection.parameters_depends_on.map(
                    parameter => {
                        const newParameter = {};
                        newParameter["value"] = parameter.id;
                        newParameter["text"] = `${parameter.name} (${parameter.type})`;
                        return newParameter;
                    }
                );
            }

            return parameters;
        },        
    }
}

export default suts;