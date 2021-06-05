import axios from "axios";

const reports = {
    state: {
        reports: {
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
        set_reports(state, payload) {
            state.reports.all.items = payload.items
        },
        set_report(state, payload) {
            state.reports.current.item = payload.item
        },
        set_report_display(state, payload) {
            state.reports.display = payload.display
        }
    },
    actions: {
        findAllReports({ commit }) {
            return new Promise((resolve, reject) => {
                axios
                    .get("/rest/reports")
                    .then(response => {
                        commit("set_reports", { items: response.data });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: "Couldn't retrieve reports", err: error } });
                        commit("set_reports", { items: [] });
                        reject(error);
                    })
            })
        },
        findReport({ commit }, id) {
            return new Promise((resolve, reject) => {
                commit("set_report", { item: null });
                axios
                    .get(`/rest/reports/${id}`)
                    .then(response => {
                        commit("set_report", { item: response.data });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: `Couldn't retrieve report with id ${id}`, err: error } });
                        commit("set_report", { item: null });
                        reject(error);
                    })
            })
        },
        addReport({ commit }, report) {
            return new Promise((resolve, reject) => {
                axios
                    .post('/rest/reports', report)
                    .then(response => {
                        commit("message_add", { message: { type: "info", title: "Add report", text: `Report ${response.data.description} added successful.` } });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: "An error occured while adding report", err: error } });
                        reject(error);
                    })
            })
        },
        deleteReport({ commit }, report) {
            return new Promise((resolve, reject) => {
                axios
                    .delete(`/rest/reports/${report.id}`)
                    .then(response => {
                        commit("message_add", { message: { type: "info", title: "Delete report", text: `Report ${response.data.description} deleted successful.` } });
                        commit("set_report", { item: null });
                        resolve();
                    })
                    .catch(error => {
                        commit("message_add", { message: { type: "error", text: `Couldn't delete report with id ${report.id}`, err: error } });
                        reject(error);
                    })
            })
        },
    },
    getters: {
        reports: state => {
            return state.reports;
        }
    }
}

export default reports;