<template>
  <div>
    <div class="row">
      <div class="col-6">
        <dictionaries-list></dictionaries-list>
      </div>
      <div class="col-6">
        <dictionaries-add></dictionaries-add>
        <dictionaries-detail></dictionaries-detail>
        <dictionaries-delete></dictionaries-delete>
      </div>
    </div>
  </div>
</template>

<script>
import DictionariesList from "./dictionaries-list";
import DictionariesAdd from "./dictionaries-add";
import DictionariesDetail from "./dictionaries-detail";
import DictionariesDelete from "./dictionaries-delete";

export default {
  components: {
    DictionariesList,
    DictionariesAdd,
    DictionariesDetail,
    DictionariesDelete
  },
  methods: {
    navigate() {
      if (this.$route.params.id) {
        this.$store.dispatch("findDictionary", this.$route.params.id);
      } else {
        this.$store.commit("set_dictionary", { item: null });
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
