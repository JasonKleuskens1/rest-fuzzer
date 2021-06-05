<template>
  <div>
    <div class="row">
      <div class="col-6">
        <tasks-active></tasks-active>
        <div style="height:13px;"></div>
        <tasks-archive></tasks-archive>
      </div>
      <div class="col-6">
        <tasks-detail></tasks-detail>
      </div>
    </div>
    <tasks-delete></tasks-delete>
  </div>
</template>

<script>
import TasksActive from "./tasks-active";
import TasksArchive from "./tasks-archive";
import TasksDetail from "./tasks-detail";
import TasksDelete from "./tasks-delete";

export default {
  components: { TasksActive, TasksArchive, TasksDetail, TasksDelete },
  methods: {
    navigate() {
      if (this.$route.params.id) {
        this.$store.dispatch("findTask", this.$route.params.id);
      } else {
        this.$store.commit("set_task", { item: null });
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