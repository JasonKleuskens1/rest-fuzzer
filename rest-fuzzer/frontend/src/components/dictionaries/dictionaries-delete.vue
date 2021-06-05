<template>
  <b-modal id="dictionaries-delete" ref="modal" v-if="this.dictionary" size="xs">
    <template slot="modal-header">
      <h6>
        <b-icon icon="trash" font-scale="1"></b-icon>&nbsp;Delete dictionary
      </h6>
    </template>

    <template slot="default">Are you sure you want to delete dictionary '{{this.dictionary.name}}'?</template>

    <template slot="modal-footer" slot-scope="{ cancel }">
      <div class="button-group-right">
        <b-button size="sm" variant="outline-danger" @click="deleteDictionary()">
          <b-icon icon="trash" font-scale="1"></b-icon>&nbsp; delete
        </b-button>
        <b-button size="sm" variant="outline-secondary" @click="cancel()">
          <b-icon icon="backspace" font-scale="1"></b-icon>&nbsp; cancel
        </b-button>
      </div>
    </template>
  </b-modal>
</template>

<script>
export default {
  data() {
    return {};
  },
  computed: {
    dictionary() {
      return this.$store.getters.dictionaries.current.item;
    }
  },
  methods: {
    deleteDictionary() {
      this.$store.dispatch("deleteDictionary", this.dictionary).then(() => {
        this.$router.push({ name: "dictionaries" });
        this.$store.commit("set_dictionary", { dictionary: null });
        this.$store.dispatch("findAllDictionaries");
      });
    }
  }
};
</script>