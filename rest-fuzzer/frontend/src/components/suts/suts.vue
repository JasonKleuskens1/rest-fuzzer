<template>
  <div>
    <div class="row">
      <div class="col-6">
        <suts-list></suts-list>
      </div>
      <div class="col-6">
        <suts-add></suts-add>
        <suts-detail></suts-detail>
      </div>
    </div>
    <suts-delete></suts-delete>
  </div>
</template>

<script>
import SutsAdd from "./suts-add";
import SutsList from "./suts-list";
import SutsDetail from "./suts-detail";
import SutsDelete from "./suts-delete";

export default {
  components: {
    SutsAdd,
    SutsList,
    SutsDetail,
    SutsDelete
  },
  methods: {
    navigate() {
      if (this.$route.params.id) {
        this.$store.dispatch("findSut", this.$route.params.id);
      } else {
        this.$store.commit("set_sut", { item: null });
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