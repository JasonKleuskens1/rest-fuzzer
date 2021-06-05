<template>
  <b-card v-if="display" header-tag="header" footer-tag="footer">
    <template v-slot:header>
      <b-icon icon="plus" font-scale="1"></b-icon>&nbsp;Add dictionary
    </template>

    <b-card-text>
      <b-form>
        <b-form-group id="input-group-1" label="Name:" label-for="input-1" description="Name">
          <b-form-input id="input-1" v-model="dictionary.name" placeholder="enter the name"></b-form-input>
        </b-form-group>

        <b-form-group
          id="input-group-2"
          label="Items:"
          label-for="input-2"
          description="Items for the dictionary (new value on each line)"
        >
          <b-form-textarea
            id="input-2"
            v-model="dictionary.itemsText"
            placeholder="enter the items"
            rows="8"
          ></b-form-textarea>
        </b-form-group>
      </b-form>
    </b-card-text>

    <template v-slot:footer>
      <div class="button-group-right">
        <b-button size="sm" variant="primary" @click="add()">
          <b-icon icon="plus" font-scale="1"></b-icon>&nbsp;add
        </b-button>
        <b-button size="sm" variant="outline-secondary" @click="cancel()">
          <b-icon icon="backspace" font-scale="1"></b-icon>&nbsp;cancel
        </b-button>
      </div>
    </template>
  </b-card>
</template>

<script>
export default {
  data() {
    return {
      dictionary: {
        name: null,
        itemsText: null
      }
    };
  },
  computed: {
    display() {
      return (
        this.$store.getters.dictionaries.display !== null &&
        this.$store.getters.dictionaries.display === "add"
      );
    }
  },
  methods: {
    reset() {
      this.dictionary.name = "";
      this.dictionary.itemsText = "";
    },
    cancel() {
      this.reset();
      this.$store.commit("set_dictionary_display", { display: null });
    },
    add() {
      this.$store.dispatch("addDictionary", this.dictionary).then(() => {
        this.cancel();
        this.$store.dispatch("findAllDictionaries");
      });
    }
  }
};
</script>
