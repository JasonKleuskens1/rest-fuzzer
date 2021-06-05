<template>
  <b-card v-if="report !== null" header-tag="header">
    <template v-slot:header>
      <b-icon icon="eye" font-scale="1"></b-icon>&nbsp;Detail report
    </template>

    <b-card-text>
      <div v-if="!report" class="text-center text-primary my-2">
        <b-spinner type="border" class="align-middle" small></b-spinner>
        <span style="margin-left:10px;">Loading...</span>
      </div>

      <b-tabs v-if="report" nav-tabs card>
        <b-tab title="Information" active>
          <div class="row">
            <div class="col">
              <div class="button-group-left">
                <b-button
                  :disabled="tasksQueuedOrRunning"
                  size="sm"
                  type="submit"
                  variant="primary"
                  title="start task to generate report"
                  v-on:click="addGenerateTask"
                >
                  <b-icon icon="play" font-scale="1"></b-icon>&nbsp;generate report
                </b-button>
                <b-button
                  size="sm"
                  variant="primary"
                  title="go to project"
                  :to="{ name: 'project', params: { id: report.project.id } }"
                >
                  <b-icon icon="link45deg" font-scale="1"></b-icon>&nbsp;
                  go to project
                </b-button>
                <b-button size="sm" to="/tasks" variant="primary" title="go to tasks">
                  <b-icon icon="link45deg" font-scale="1"></b-icon>&nbsp;go to tasks
                </b-button>
                <b-button
                  size="sm"
                  type="submit"
                  v-b-modal.reports-delete
                  variant="outline-danger"
                  title="delete this report"
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
                <dd>{{report.id}}</dd>
                <dt>Description</dt>
                <dd>{{report.description}}</dd>
                <dt>Type</dt>
                <dd>{{report.type | enumToHuman }}</dd>
                <dt>Project</dt>
                <dd>{{report.project.description}}</dd>
                <dt>Created @</dt>
                <dd>{{report.createdAt | date }}</dd>
                <dt>Completed @</dt>
                <dd>{{report.completedAt | date }}</dd>
              </dl>
            </div>
            <div class="col">
              <dl class="dl-horizontal">
                <dt>Meta data</dt>
                <dd>
                  <div class="json fixed" :inner-html.prop="report.metaDataTuplesJson | json"></div>
                </dd>
              </dl>
            </div>
          </div>
        </b-tab>
        <b-tab v-if="report.completedAt" title="Output">
          <div class="row">
            <div class="col">
              <div class="button-group-left">
                <b-button
                  size="sm"
                  type="submit"
                  variant="primary"
                  title="copy output of report to clipboard"
                  @click="copy"
                >
                  <b-icon icon="clipboard" font-scale="1"></b-icon>&nbsp;copy output to clipboard
                </b-button>

                <b-button
                  size="sm"
                  type="submit"
                  variant="primary"
                  title="download"
                  :download="getDownload()"
                  :href="getHref()"
                >
                  <b-icon icon="download" font-scale="1"></b-icon>&nbsp;download
                </b-button>
              </div>
            </div>
          </div>
          <div class="row">
            <div class="col">
              <dl class="dl-horizontal">
                <dt>Raw</dt>
                <dd>
                  <div class="data fixed">{{ report.output }}</div>
                </dd>
              </dl>
            </div>
          </div>
        </b-tab>
      </b-tabs>
    </b-card-text>
  </b-card>
</template>

<script>
import Constants from "../../shared/constants";

export default {
  data() {
    return {};
  },
  methods: {
    refreshReport() {
      if (!this.tasksQueuedOrRunning) {
        this.$timer.stop("refreshReport");
        this.$store.dispatch("findReport", this.report.id);
        return;
      }

      this.$store
        .dispatch("countReportRunningOrQueuedTasks", {
          id: this.report.id
        })
        .catch(error => {
          this.$timer.stop("refreshReport");
        });
    },
    addGenerateTask() {
      this.$store
        .dispatch("addTask", {
          name: Constants.TASK_REPORTER,
          metaDataTuples: { report_id: this.report.id }
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
    },
    getDownload() {
      return `${this.$options.filters.toFilename(this.report.description)}.tex`;
    },
    getHref() {
      return (
        "data:application/octet-stream;charset=utf-8," + encodeURIComponent(this.report.output)
      );
    },
    copy() {
      this.$copyText(this.report.output).then(
        function(e) {
          // succes
        },
        function(e) {
          // fail
        }
      );
    }
  },
  computed: {
    report() {
      return this.$store.getters.reports.current.item;
    },
    tasksQueuedOrRunning() {
      return (
        this.$store.getters.reports.current.queued_or_running_tasks_count !==
          null &&
        this.$store.getters.reports.current.queued_or_running_tasks_count > 0
      );
    },
    canExecuteTask() {
      return true;
    }
  },
  timers: {
    refreshReport: {
      time: Constants.REFRESH_TIMEOUT,
      autostart: false,
      repeat: true
    }
  }
};
</script>