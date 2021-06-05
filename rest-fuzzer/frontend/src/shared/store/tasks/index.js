import axios from "axios";

import Constants from "../../constants";

function convertTasks(tasks) {
    if (tasks != null) {
        return tasks.map(t => convertTask(t));
    }
}

function convertTask(task) {
    if (task == null) {
        return task;
    }

    let nameParts = task.canonicalName.split(".");

    let status = Constants.TASK_STATUS_QUEUED;
    if (task.startedAt != null && task.crashedAt == null && task.finishedAt == null) {
        status = Constants.TASK_STATUS_RUNNING;
    } else if (task.startedAt != null && task.crashedAt != null) {
        status = Constants.TASK_STATUS_CRASHED;
    } else if (task.startedAt != null && task.finishedAt != null) {
        status = Constants.TASK_STATUS_FINISHED;
    }

    let endedAt = null;
    if (task.crashedAt != null) { endedAt = task.crashedAt };
    if (task.finishedAt != null) { endedAt = task.finishedAt };

    task["name"] = nameParts[nameParts.length - 1].replace('Task', '');
    task["status"] = status;
    task["endedAt"] = endedAt;

    return task;
}

const tasks = {
    state: {
        tasks: {
            active: {
                list: null
            },
            archive: {
                list: null,
                count: null
            },
            current: {
                item: null
            }
        }
    },
    mutations: {
        set_tasks_active_list(state, payload) {
            state.tasks.active.list = convertTasks(payload.list)
        },

        set_tasks_archive_list(state, payload) {
            state.tasks.archive.list = convertTasks(payload.list)
        },
        set_tasks_archive_count(state, payload) {
            state.tasks.archive.count = payload.count
        },

        set_task(state, payload) {
            state.tasks.current.item = convertTask(payload.item)
        }
    },
    actions: {
        findTasksActive({ commit }) {
            return new Promise((resolve, reject) => {
                axios
                    .get("/rest/tasks/active")
                    .then(response => {
                        commit("set_tasks_active_list", { list: response.data });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: "Couldn't retrieve tasks (active)", err: error } });
                        commit("set_tasks_active_list", { list: [] });
                        reject(error);
                    })
            })
        },
        findTasksArchive({ commit }, data) {
            let queryParams = `?curPage=${data.context.currentPage}&perPage=${data.context.perPage}`;
            return new Promise((resolve, reject) => {
                axios
                    .get(`/rest/tasks/archive/${queryParams}`)
                    .then(response => {
                        commit("set_tasks_archive_list", { list: response.data });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: "Couldn't retrieve tasks (archive)", err: error } });
                        commit("set_tasks_archive_list", { list: [] });
                        reject(error);
                    })
            })
        },
        countTasksArchive({ commit }) {
            return new Promise((resolve, reject) => {
                axios
                    .get(`/rest/tasks/archive/count`)
                    .then(response => {
                        commit("set_tasks_archive_count", { count: response.data });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: "Couldn't count tasks (archive)", err: error } });
                        commit("set_tasks_archive_count", { count: null });
                        reject(error);
                    })
            })
        },
        findTask({ commit }, id) {
            return new Promise((resolve, reject) => {
                axios
                    .get(`/rest/tasks/${id}`)
                    .then(response => {
                        commit("set_task", { item: response.data });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: `Couldn't retrieve task id ${id}`, err: error } });
                        commit("set_task", { item: null });
                        reject(error);
                    })
            })
        },
        addTask({ commit }, data) {
            return new Promise((resolve, reject) => {
                axios
                    .post(`/rest/tasks/${data.name}/start`, data.metaDataTuples)
                    .then(response => {
                        commit("message_add", { message: { type: "info", title: "Add task", text: `Task (${data.name}) added successful.` } });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: `Couldn't add task (${data.name})`, err: error } });
                        reject(error);
                    })
            })
        },
        deleteTask({ commit }, task) {
            return new Promise((resolve, reject) => {
                axios
                    .delete(`/rest/tasks/${task.id}`)
                    .then(response => {
                        commit("message_add", { message: { type: "info", title: "Delete task", text: `Task ${response.data.id} deleted successful.` } });
                        commit("set_task", { item: null });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: `Couldn't task with id ${task.id}`, err: error } });
                        reject(error);
                    })
            })
        },
    },
    getters: {
        tasks: state => {
            return state.tasks
        },
    }
}

export default tasks;