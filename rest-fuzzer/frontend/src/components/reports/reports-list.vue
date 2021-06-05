<template>
  <div>
    <b-card header-tag="header">
      <template v-slot:header>
        <b-icon icon="clipboard-data" font-scale="1"></b-icon>&nbsp;Reports (data)
      </template>
      <b-card-text>
        <div class="button-group-left">
          <b-button size="sm" type="submit" variant="primary" @click="add">
            <b-icon icon="plus" font-scale="1"></b-icon>&nbsp;add
          </b-button>
        </div>
        <list
          @click-item="select"
          :select="false"
          :fields="fields"
          :items="projects"
          :formatters="formatters"
        ></list>
      </b-card-text>
    </b-card>
  </div>
</template>

<script>
import List from "../shared/list/list";

export default {
  components: { List },
  data() {
    return {
      formatters: [
        { field: "type", as: "enumToHuman" },
        { field: "createdAt", as: "dateShort" },
        { field: "completedAt", as: "dateShort" }
      ],
      fields: [
        { key: "id", label: "#", thStyle: "width: 50px;" },
        { key: "type" },
        { key: "description" },
        { key: "project.id", label: "Project #" },
        { key: "createdAt", label: "Created @", thStyle: "width: 110px;" },
        { key: "completedAt", label: "Completed @", thStyle: "width: 110px;" }
      ]
    };
  },
  methods: {
    select(report) {
      this.$router.push({ name: "report", params: { id: report.id } });
      this.$store.commit("set_report_display", { display: null });
      this.$store.dispatch("findReport", report.id);
    },
    add() {
      this.$store.commit("set_report", { item: null });
      this.$store.commit("set_report_display", { display: "add" });
    }
  },
  computed: {
    projects() {
      return this.$store.getters.reports.all.items;
    }
  },
  created: function() {
    this.$store.dispatch("findAllReports");
  }
};
</script>
