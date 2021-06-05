<template>
  <div>
    <div class="row">
      <div class="col-6">
        <projects-list></projects-list>
      </div>
      <div class="col-6">
        <projects-add></projects-add>
        <projects-detail></projects-detail>
      </div>
    </div>
    <projects-clear></projects-clear>
    <projects-delete></projects-delete>
  </div>
</template>

<script>
import ProjectsAdd from "./projects-add";
import ProjectsList from "./projects-list";
import ProjectsDetail from "./projects-detail";
import ProjectsClear from "./projects-clear";
import ProjectsDelete from "./projects-delete";

export default {
  components: {
    ProjectsAdd,
    ProjectsList,
    ProjectsDetail,
    ProjectsClear,
    ProjectsDelete
  },
  methods: {
    navigate() {
      if (this.$route.params.id) {
        this.$store.dispatch("findProject", this.$route.params.id);
      } else {
        this.$store.commit("set_project", { item: null });
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
