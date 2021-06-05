<template>
  <b-card v-if="sut" header-tag="header">
    <template v-slot:header>
      <b-icon icon="eye" font-scale="1"></b-icon>&nbsp;Detail system under test
    </template>

    <b-card-text>
      <div v-if="this.sut === null" class="text-center text-primary my-2">
        <b-spinner type="border" class="align-middle" small></b-spinner>
        <span style="margin-left:10px;">Loading...</span>
      </div>

      <b-tabs v-if="this.sut !== null" nav-tabs card>
        <b-tab title="Information" active>
          <b-row>
            <b-col>
              <div class="button-group-left">
                <b-button
                  :disabled="tasksQueuedOrRunning"
                  size="sm"
                  type="submit"
                  variant="primary"
                  title="start task to extract REST model description (actions and dependencies) from OAS"
                  v-on:click="addExtractorTask"
                >
                  <b-icon icon="play" font-scale="1"></b-icon>&nbsp;start extract task
                </b-button>
                <b-button size="sm" to="/tasks" variant="primary" title="go to tasks">
                  <b-icon icon="link45deg" font-scale="1"></b-icon>&nbsp;go to tasks
                </b-button>
                <b-button
                  size="sm"
                  type="submit"
                  v-b-modal.suts-delete
                  variant="outline-danger"
                  title="delete this SUT"
                >
                  <b-icon icon="trash" font-scale="1"></b-icon>&nbsp;delete
                </b-button>
              </div>
            </b-col>
          </b-row>
          <b-row>
            <b-col>
              <dl class="dl-horizontal">
                <dt>Identifier</dt>
                <dd>{{this.sut.id}}</dd>
                <dt>Description</dt>
                <dd>{{this.sut.description ? this.sut.description : '-'}}</dd>
                <dt>Title</dt>
                <dd>{{this.sut.title ? this.sut.title : '-'}}</dd>
                <dt>OAS location</dt>
                <dd>
                  <b-link :href="this.sut.location" target="_blank">{{this.sut.location}}</b-link>
                </dd>
                <dt>Created @</dt>
                <dd>{{this.sut.createdAt | date }}</dd>
              </dl>
            </b-col>
          </b-row>
        </b-tab>
        <b-tab :disabled="this.actionsCount === 0" :title="actionsTitle">
          <SutsActions :sut="sut" :fields="actions_fields" :formatters="actions_formatters"></SutsActions>
        </b-tab>
        <b-tab :disabled="this.parametersCount === 0" :title="parametersTitle">
          <SutsParameters
            :sut="sut"
            :fields="parameters_fields"
            :formatters="parameters_formatters"
          ></SutsParameters>
        </b-tab>
        <b-tab :disabled="this.actionsCount === 0" :title="actionsDependenciesTitle">
          <SutsActionsDependencies
            :sut="sut"
            :fields="actions_dependencies_fields"
            :formatters="actions_dependencies_formatters"
          ></SutsActionsDependencies>
        </b-tab>
      </b-tabs>
    </b-card-text>
  </b-card>
</template>

<script>
import Constants from "../../shared/constants";

import SutsActions from "./suts-actions";
import SutsParameters from "./suts-parameters";
import SutsActionsDependencies from "./suts-actions-dependencies";

export default {
  components: { SutsActions, SutsParameters, SutsActionsDependencies },
  data() {
    return {
      tab: 0,
      actions_formatters: [],
      actions_fields: [
        { key: "id", label: "#", thStyle: "width: 50px;" },
        { key: "httpMethod", label: "HTTP method", thStyle: "width: 110px;" },
        { key: "path", label: "Path" },
        { key: "details", label: "Details", thStyle: "width: 60px;" }
      ],
      parameters_formatters: [],
      parameters_fields: [
        { key: "id", label: "#", thStyle: "width: 50px;" },
        {
          key: "action.httpMethod",
          label: "HTTP method",
          thStyle: "width: 110px;"
        },
        { key: "action.path", label: "Path" },
        { key: "name" },
        { key: "context", thStyle: "width: 80px;" },
        { key: "type", thStyle: "width: 80px;" },
        { key: "details", label: "Details", thStyle: "width: 60px;" }
      ],
      actions_dependencies_formatters: [],
      actions_dependencies_fields: [
        { key: "id", label: "#", thStyle: "width: 50px;" },
        {
          key: "action.httpMethod",
          label: "HTTP method",
          thStyle: "width: 110px;"
        },
        { key: "action.path", label: "Path" },
        {
          key: "discoveryModus",
          label: "Discovered",
          thStyle: "width: 110px;"
        },
        { key: "details", label: "Details", thStyle: "width: 60px;" }
      ]
    };
  },
  methods: {
    refreshSut() {
      if (!this.tasksQueuedOrRunning) {
        this.$timer.stop("refreshSut");
        this.$store.dispatch("findSut", this.sut.id);
        this.$store.dispatch("findAllSuts");
        this.$root.$emit("bv::refresh::table", "sut-actions");
        this.$root.$emit("bv::refresh::table", "sut-parameters");
        this.$root.$emit("bv::refresh::table", "sut-actions-dependencies");
        return;
      }

      this.$store
        .dispatch("countSutRunningOrQueuedTasks", this.sut.id)
        .catch(error => {
          this.$timer.stop("refreshSut");
        });
    },
    addExtractorTask() {
      this.$store
        .dispatch("addTask", {
          name: Constants.TASK_EXTRACTOR,
          metaDataTuples: { sut_id: this.sut.id }
        })
        .then(() => {
          this.$store
            .dispatch("countSutRunningOrQueuedTasks", this.sut.id)
            .then(() => {
              this.$timer.start("refreshSut");
            });
        });
    }
  },
  computed: {
    sut() {
      return this.$store.getters.suts.current.item;
    },
    tasksQueuedOrRunning() {
      return (
        this.$store.getters.suts.current.queued_or_running_tasks_count !==
          null &&
        this.$store.getters.suts.current.queued_or_running_tasks_count > 0
      );
    },
    actionsTitle() {
      let title = "Actions";
      if (this.actionsCount > 0) {
        title += ` [${this.actionsCount}]`;
      }
      return title;
    },
    actionsCount() {
      const count = this.$store.getters.suts.current.actions.total;
      return count !== null && count > 0 ? count : 0;
    },
    parametersTitle() {
      let title = "Parameters";
      if (this.parametersCount > 0) {
        title += ` [${this.parametersCount}]`;
      }
      return title;
    },
    parametersCount() {
      const count = this.$store.getters.suts.current.parameters.total;
      return count !== null && count > 0 ? count : 0;
    },
    actionsDependenciesTitle() {
      let title = "Dependencies";
      if (this.actionsDependenciesCount > 0) {
        title += ` [${this.actionsDependenciesCount}]`;
      }
      return title;
    },
    actionsDependenciesCount() {
      const count = this.$store.getters.suts.current.actions_dependencies.total;
      return count !== null && count > 0 ? count : 0;
    }
  },
  timers: {
    refreshSut: {
      time: Constants.REFRESH_TIMEOUT,
      autostart: false,
      repeat: true
    }
  }
};
</script>