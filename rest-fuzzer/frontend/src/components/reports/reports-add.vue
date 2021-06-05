<template>
  <b-card v-if="display" header-tag="header" footer-tag="footer">
    <template v-slot:header>
      <b-icon icon="plus" font-scale="1"></b-icon>&nbsp;Add report
    </template>

    <b-card-text>
      <b-alert show variant="danger" v-if="projectsForSelection.length === 0">
        No projects available. Please
        <b-link to="projects" size="sm" type="submit" variant="primary">add</b-link>&nbsp;a project first.
      </b-alert>

      <b-form>
        <b-form-group label="Description:" label-for="description" description="Describe report">
          <b-form-textarea id="description" v-model="report.description" required></b-form-textarea>
        </b-form-group>

        <hr />

        <b-form-group label="Type:" label-for="type" description="Select type">
          <b-form-select id="type" :options="types" v-model="report.type" required>
            <template v-slot:first>
              <b-form-select-option :value="null" disabled>-- select a type --</b-form-select-option>
            </template>
          </b-form-select>
        </b-form-group>

        <hr />

        <b-form-group
          v-if="projectsForSelection.length > 0"
          label="Project:"
          description="Select project"
        >
          <b-form-select
            id="type"
            :options="projectsForSelection"
            v-model="report.project.id"
            required
          >
            <template v-slot:first>
              <b-form-select-option :value="null" disabled>-- select a project --</b-form-select-option>
            </template>
          </b-form-select>
          <hr />
        </b-form-group>

        <div>
          <b-form-group
            label="Interval (responses):"
            label-for="points-interval"
            description="Set interval points responses"
          >
            <b-form-input
              id="points-interval"
              v-model="metaDataTuplesJson.pointsInterval"
              type="range"
              min="10"
              max="1000"
              step="5"
            ></b-form-input>
            <div class="mt-2">Points interval: {{ metaDataTuplesJson.pointsInterval }}</div>
          </b-form-group>

          <hr />
        </div>

        <div>
          <b-form-group
            label="Grid interval x-axis:"
            label-for="x-tick-interval"
            description="Set interval grid x-axis"
          >
            <b-form-input
              id="x-tick-interval"
              v-model="metaDataTuplesJson.xTickInterval"
              type="range"
              min="10"
              max="2500"
              step="10"
            ></b-form-input>
            <div class="mt-2">Grid interval x-axis: {{ metaDataTuplesJson.xTickInterval }}</div>
          </b-form-group>

          <hr />
        </div>

        <div>
          <b-form-group
            label="Grid interval y-axis:"
            label-for="y-tick-interval"
            description="Set interval grid y-axis"
          >
            <b-form-input
              id="y-tick-interval"
              v-model="metaDataTuplesJson.yTickInterval"
              type="range"
              min="10"
              max="2500"
              step="10"
            ></b-form-input>
            <div class="mt-2">Grid interval y-axis: {{ metaDataTuplesJson.yTickInterval }}</div>
          </b-form-group>

          <hr />
        </div>
      </b-form>
    </b-card-text>

    <template v-slot:footer>
      <div class="button-group-right">
        <b-button size="sm" type="submit" variant="primary" @click="add()">
          <b-icon icon="plus" font-scale="1"></b-icon>&nbsp;add
        </b-button>
        <b-button size="sm" type="cancel" variant="outline-secondary" @click="cancel()">
          <b-icon icon="backspace" font-scale="1"></b-icon>&nbsp;cancel
        </b-button>
      </div>
    </template>
  </b-card>
</template>

<script>
import constants from "../../shared/constants";
const DEFAULT_META = {
  pointsInterval: 10,
  xTickInterval: 50,
  yTickInterval: 50
};

export default {
  data() {
    return {
      report: {
        description: null,
        type: null,
        project: {
          id: null
        },
        metaDataTuplesJson: null
      },
      pointsInterval: null,
      xTickInterval: null,
      yTickInterval: null,
      metaDataTuplesJson: DEFAULT_META,
      types: [
        { value: "CODE_COVERAGE", text: "Code coverage" },
        { value: "RESPONSES", text: "Responses" }
      ]
    };
  },
  methods: {
    reset() {
      this.report.description = null;
      this.report.type = null;
      this.metaDataTuplesJson = DEFAULT_META;
    },
    cancel() {
      this.reset();
      this.$store.commit("set_report_display", { display: null });
    },
    setMetaDataTuplesJson() {
      this.metaDataTuplesJson.pointsInterval = Number(
        this.metaDataTuplesJson.pointsInterval
      );

      this.metaDataTuplesJson.xTickInterval = Number(
        this.metaDataTuplesJson.xTickInterval
      );

      this.metaDataTuplesJson.yTickInterval = Number(
        this.metaDataTuplesJson.yTickInterval
      );

      this.report.metaDataTuplesJson = JSON.stringify(this.metaDataTuplesJson);
    },
    add() {
      this.setMetaDataTuplesJson();
      this.$store.dispatch("addReport", this.report).then(() => {
        this.cancel();
        this.$store.dispatch("findAllReports");
      });
    },
    async findAllProjects() {
      if (this.$store.getters.projects.all.items === null) {
        await this.$store.dispatch("findAllProjects");
      }
    }
  },
  computed: {
    display() {
      return (
        this.$store.getters.reports.display !== null &&
        this.$store.getters.reports.display === "add"
      );
    },
    projectsForSelection() {
      this.findAllProjects();
      return this.$store.getters.projectsForSelection;
    }
  }
};
</script>