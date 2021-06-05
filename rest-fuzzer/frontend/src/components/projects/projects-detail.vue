<template>
  <b-card v-if="project !== null" header-tag="header">
    <template v-slot:header>
      <b-icon icon="eye" font-scale="1"></b-icon>&nbsp;Detail project
    </template>

    <b-card-text>
      <div v-if="!project" class="text-center text-primary my-2">
        <b-spinner type="border" class="align-middle" small></b-spinner>
        <span style="margin-left:10px;">Loading...</span>
      </div>

      <b-tabs v-if="project" nav-tabs card>
        <b-tab title="Information" active>
          <div class="row">
            <div class="col">
              <div class="button-group-left">
                <b-button
                  :disabled="tasksQueuedOrRunning"
                  size="sm"
                  type="submit"
                  variant="primary"
                  title="start task to fuzz SUT"
                  v-on:click="addFuzzerTask"
                >
                  <b-icon icon="play" font-scale="1"></b-icon>&nbsp;start fuzzing
                </b-button>
                <b-button
                  size="sm"
                  variant="primary"
                  title="go to SUT"
                  :to="{ name: 'sut', params: { id: this.project.sut.id } }"
                >
                  <b-icon icon="link45deg" font-scale="1"></b-icon>&nbsp;
                  go to SUT
                </b-button>
                <b-button size="sm" to="/tasks" variant="primary" title="start task to fuzz SUT">
                  <b-icon icon="link45deg" font-scale="1"></b-icon>&nbsp;go to tasks
                </b-button>
                <b-button
                  size="sm"
                  type="submit"
                  v-b-modal.projects-clear
                  variant="outline-danger"
                  title="delete all requests and responses"
                >
                  <b-icon icon="trash" font-scale="1"></b-icon>&nbsp;clear results
                </b-button>
                <b-button
                  size="sm"
                  type="submit"
                  v-b-modal.projects-delete
                  variant="outline-danger"
                  title="delete this fuzzing project"
                >
                  <b-icon icon="trash" font-scale="1"></b-icon>&nbsp;delete
                </b-button>
              </div>
            </div>
          </div>
          <div class="row">
            <div class="col">
              <dl class="dl-horizontal">
                <dt>Identifier</dt>
                <dd>{{project.id}}</dd>
                <dt>Description</dt>
                <dd>{{project.description}}</dd>
                <dt>Type</dt>
                <dd>{{project.type | enumToHuman }}</dd>
                <dt>System under test</dt>
                <dd>
                  <b-link :href="project.sut.location" target="_blank">{{project.sut.location}}</b-link>
                </dd>
                <dt>Created @</dt>
                <dd>{{project.createdAt | date }}</dd>
              </dl>
            </div>
            <div class="col">
              <dl class="dl-horizontal">
                <dt>Meta data</dt>
                <dd>
                  <div class="data fixed" :inner-html.prop="project.metaDataTuplesJson | json"></div>
                </dd>
              </dl>
            </div>
          </div>
        </b-tab>
        <b-tab :disabled="!sequencesPresent" :title="sequencesTitle">
          <ProjectsSequences
            :project="project"
            :fields="sequenceFields"
            :formatters="sequenceFormatters"
          ></ProjectsSequences>
        </b-tab>
        <b-tab :disabled="!responsesPresent" :title="responsesTitle">
          <ProjectsResponses
            :project="project"
            :fields="responseFields"
            :formatters="responseFormatters"
          ></ProjectsResponses>
        </b-tab>
        <b-tab :disabled="!requestsPresent" :title="requestsTitle">
          <ProjectsRequests
            :project="project"
            :fields="requestFields"
            :formatters="requestFormatters"
          ></ProjectsRequests>
        </b-tab>
      </b-tabs>
    </b-card-text>
  </b-card>
</template>

<script>
import Constants from "../../shared/constants";

import ProjectsSequences from "./projects-sequences";
import ProjectsRequests from "./projects-requests";
import ProjectsResponses from "./projects-responses";

export default {
  components: {
    ProjectsSequences,
    ProjectsRequests,
    ProjectsResponses
  },
  data() {
    return {
      sequenceFormatters: [],
      sequenceFields: [
        { key: "id", label: "#", thStyle: "width: 50px;" },
        { key: "position", label: "Execution order" },
        { key: "length", label: "Number of requests" },
        { key: "status" },
        { key: "details", label: "Details", thStyle: "width: 60px;" }
      ],
      requestFormatters: [{ field: "createdAt", as: "dateShort" }],
      requestFields: [
        { key: "id", label: "#", thStyle: "width: 50px;" },
        { key: "path" },
        { key: "httpMethod", label: "Http method", thStyle: "width: 110px;" },
        { key: "createdAt", label: "Created @", thStyle: "width: 90px;" },
        { key: "details", label: "Details", thStyle: "width: 60px;" }
      ],
      responseFormatters: [{ field: "createdAt", as: "dateShort" }],
      responseFields: [
        { key: "id", label: "#", thStyle: "width: 50px;" },
        { key: "request.path", label: "Path" },
        {
          key: "request.httpMethod",
          label: "HTTP method",
          thStyle: "width: 110px;"
        },
        { key: "statusCode", label: "HTTP status", thStyle: "width: 110px;" },
        { key: "createdAt", label: "Created @", thStyle: "width: 90px;" },
        { key: "details", label: "Details", thStyle: "width: 60px;" }
      ]
    };
  },
  methods: {
    refreshProject() {
      if (!this.tasksQueuedOrRunning) {
        this.$timer.stop("refreshProject");
        this.$store.dispatch("findProject", this.project.id);
        this.$root.$emit("bv::refresh::table", "project-requests");
        this.$root.$emit("bv::refresh::table", "project-responses");
        return;
      }

      this.$store
        .dispatch("countProjectRunningOrQueuedTasks", {
          id: this.project.id
        })
        .catch(error => {
          this.$timer.stop("refreshProject");
        });
    },
    addFuzzerTask() {
      this.$store
        .dispatch("addTask", {
          name: Constants.TASK_FUZZER,
          metaDataTuples: { project_id: this.project.id }
        })
        .then(() => {
          this.$store
            .dispatch("countProjectRunningOrQueuedTasks", {
              id: this.project.id
            })
            .then(() => {
              this.$timer.start("refreshProject");
            });
        });
    }
  },
  computed: {
    project() {
      return this.$store.getters.projects.current.item;
    },
    tasksQueuedOrRunning() {
      return (
        this.$store.getters.projects.current.queued_or_running_tasks_count !==
          null &&
        this.$store.getters.projects.current.queued_or_running_tasks_count > 0
      );
    },
    sequencesPresent() {
      return this.sequencesCount > 0;
    },
    sequencesTitle() {
      let title = "Sequences";
      if (this.sequencesCount > 0) {
        title += ` [${this.sequencesCount}]`;
      }
      return title;
    },
    sequencesCount() {
      const count = this.$store.getters.projects.current.sequences.total;
      return count !== null && count > 0 ? count : 0;
    },
    requestsPresent() {
      return this.requestsCount > 0;
    },
    requestsTitle() {
      let title = "Requests";
      if (this.requestsCount > 0) {
        title += ` [${this.requestsCount}]`;
      }
      return title;
    },
    requestsCount() {
      const count = this.$store.getters.projects.current.requests.total;
      return count !== null && count > 0 ? count : 0;
    },
    responsesPresent() {
      return this.responsesCount > 0;
    },
    responsesTitle() {
      let title = "Responses";
      if (this.responsesCount > 0) {
        title += ` [${this.responsesCount}]`;
      }
      return title;
    },
    responsesCount() {
      const count = this.$store.getters.projects.current.responses.total;
      return count !== null && count > 0 ? count : 0;
    },
    canExecuteTask() {
      return true;
    }
  },
  timers: {
    refreshProject: {
      time: Constants.REFRESH_TIMEOUT,
      autostart: false,
      repeat: true
    }
  }
};
</script>