<template>
  <div>
    <div class="row">
      <div class="col-6">
        <configurations-list></configurations-list>
      </div>
      <div class="col-6">
        <configurations-add></configurations-add>
        <configurations-detail></configurations-detail>
        <configurations-delete></configurations-delete>
      </div>
    </div>
  </div>
</template>

<script>
import ConfigurationsList from "./configurations-list";
import ConfigurationsAdd from "./configurations-add";
import ConfigurationsDetail from "./configurations-detail";
import ConfigurationsDelete from "./configurations-delete";

export default {
  components: {
    ConfigurationsList,
    ConfigurationsAdd,
    ConfigurationsDetail,
    ConfigurationsDelete
  },
  methods: {
    navigate() {
      if (this.$route.params.id) {
        this.$store.dispatch("findConfiguration", this.$route.params.id);
      } else {
        this.$store.commit("set_configuration", { item: null });
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