<template>
  <div>
    <div class="row">
      <div class="col-6">
        <reports-list></reports-list>
      </div>
      <div class="col-6">
        <reports-add></reports-add>
        <reports-detail></reports-detail>
      </div>
    </div>
    <reports-delete></reports-delete>
  </div>
</template>

<script>
import ReportsAdd from "./reports-add";
import ReportsList from "./reports-list";
import ReportsDetail from "./reports-detail";
import ReportsDelete from "./reports-delete";

export default {
  components: {
    ReportsAdd,
    ReportsList,
    ReportsDetail,
    ReportsDelete
  },
  methods: {
    navigate() {
      if (this.$route.params.id) {
        this.$store.dispatch("findReport", this.$route.params.id);
      } else {
        this.$store.commit("set_report", { item: null });
      }
    }
  },
  watch: {
    $route(to, from) {
      this.navigate();
    }
  },
  created() {
    this.navigate();
  }
};
</script>
