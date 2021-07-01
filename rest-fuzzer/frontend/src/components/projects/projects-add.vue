<template>
  <b-card v-if="display" header-tag="header" footer-tag="footer">
    <template v-slot:header>
      <b-icon icon="plus" font-scale="1"></b-icon>&nbsp;Add project
    </template>

    <b-card-text>
      <b-alert show variant="warning" v-if="configurationsForSelection.length === 0">
        No configurations available. Please
        <b-link to="configurations" size="sm" type="submit" variant="primary">add</b-link>&nbsp;a configuration first.
      </b-alert>

      <b-alert show variant="danger" v-if="sutsForSelection.length === 0">
        No suts available. Please
        <b-link to="suts" size="sm" type="submit" variant="primary">add</b-link>&nbsp;a sut first and make sure the extractor task is executed.
      </b-alert>

      <b-alert
        show
        variant="danger"
        v-if="dictionariesForSelection.length === 0 && (isTypeDict || isTypeMbDict)"
      >
        No dictionaries available. Please
        <b-link to="dictionaries" size="sm" type="submit" variant="primary">add</b-link>&nbsp;a least one dictionary.
      </b-alert>

      <b-form>
        <b-form-group label="Description:" label-for="description" description="Describe project">
          <b-form-textarea id="description" v-model="project.description" required></b-form-textarea>
        </b-form-group>

        <hr />

        <b-form-group label="Type:" label-for="type" description="Select type">
          <b-form-select id="type" :options="types" v-model="project.type" required>
            <template v-slot:first>
              <b-form-select-option :value="null" disabled>-- select a type --</b-form-select-option>
            </template>
          </b-form-select>
        </b-form-group>

        <hr />

        <b-form-group
          v-if="configurationsForSelection.length > 0"
          label="Configuration:"
          description="Configuration for project (select none or one), configuration is copied to this project"
        >
          <b-form-checkbox
            switch
            v-for="config in configurationsForSelection"
            v-model="configuration"
            :key="config.value"
            :value="config.value"
          >{{ config.text }}</b-form-checkbox>
          <hr />
        </b-form-group>

        <b-form-group
          v-if="isTypeBasic || isTypeDict"
          label="Repetitions:"
          label-for="input-2"
          description="Set number of repetitions"
        >
          <b-form-input
            id="range-1"
            v-model="metaDataTuplesJson.repetitions"
            type="range"
            min="1"
            step="1"
            max="1000"
          ></b-form-input>
          <div class="mt-2">Repetitions: {{ metaDataTuplesJson.repetitions }}</div>
          <hr />
        </b-form-group>

        <div v-if="isTypeMb || isTypeMbWhitebox">
          <b-form-group
            label="Maximum sequence length:"
            label-for="sequence-length"
            description="Set maximum length of generated sequences"
          >
            <b-form-input
              id="sequence-length"
              v-model="metaDataTuplesJson.maxSequenceLength"
              type="range"
              min="1"
              max="10"
            ></b-form-input>
            <div class="mt-2">Maximum sequence length: {{ metaDataTuplesJson.maxSequenceLength }}</div>
          </b-form-group>

          <hr />
        </div>

        <div v-if="isTypeMbBlackbox || isTypeMbWhitebox">
          <b-form-group
            label="Execution time in minutes:"
            label-for="sequence-length"
            description="Set the time in minutes that the fuzzer should run"
          >
            <b-form-input
              id="sequence-length"
              v-model="metaDataTuplesJson.duration"
              type="range"
              min="1"
              max="1440"
            ></b-form-input>
            <div class="mt-2">Duration: {{ metaDataTuplesJson.duration }} minutes</div>
          </b-form-group>

          <hr />
        </div>

        

        <div v-if="isTypeMbWhitebox">
          <b-form-group
            label="Required non-fuzzable threshold value:"
            label-for="required-nonfuzzable-threshold-score"
            description="The threshold score before making parameter required non-fuzzable"
          >
            <b-form-input
              id="sequence-length"
              v-model="metaDataTuplesJson.thresholdRequiredNonFuzzableValue"
              type="range"
              min="1"
              max="100"
            ></b-form-input>
            <div class="mt-2">Required non-fuzzable threshold score: {{ metaDataTuplesJson.thresholdRequiredNonFuzzableValue }}</div>
          </b-form-group>

          <hr />
        </div>
        
        <div v-if="isTypeMbWhitebox">
          <b-form-group
            label="Required non-fuzzable min. executed tests threshold:"
            label-for="required-nonfuzzable-threshold-min-requests"
            description="Minimum of performed tests before making parameters required non-fuzzable"
          >
            <b-form-input
              id="sequence-length"
              v-model="metaDataTuplesJson.thresholdRequiredNonFuzzableMinimumRequests"
              type="range"
              min="1"
              max="100"
            ></b-form-input>
            <div class="mt-2">Required non-fuzzable threshold minimum requests: {{ metaDataTuplesJson.thresholdRequiredNonFuzzableMinimumRequests }}</div>
          </b-form-group>

          <hr />
        </div>

        <div v-if="!isTypeMbBlackbox && !isTypeMbWhitebox">
          <b-form-group
            label="Maximum number of requests:"
            label-for="max-requests"
            description="Set maximum of requests to be executed"
          >
            <b-form-input
              id="max-requests"
              v-model="metaDataTuplesJson.maxNumRequests"
              type="range"
              min="1000"
              max="500000"
              step="1000"
            ></b-form-input>
            <div class="mt-2">Maximum number of requests: {{ metaDataTuplesJson.maxNumRequests }}</div>
          </b-form-group>

          <hr />
        </div>

        <b-form-group
          v-if="dictionariesForSelection.length > 0 && (isTypeDict || isTypeMbDict)"
          label="Dictionaries:"
          description="Dictionaries for project (select one or more)"
        >
          <b-form-checkbox-group
            switches
            stacked
            :options="dictionariesForSelection"
            v-model="dictionaries"
          ></b-form-checkbox-group>

          <hr />
        </b-form-group>

        <b-form-group
          v-if="isTypeDict || isTypeMbDict"
          label="Maximum parameters to adapt per action:"
          description="Select the maximum amount of paramters to use a dictionary value for (per action)"
        >
          <b-form-input
            id="max-dict-params"
            v-model="metaDataTuplesJson.maxDictParams"
            type="range"
            min="1"
            max="25"
            step="1"
          ></b-form-input>
          <div class="mt-2">Maximum parameters : {{ metaDataTuplesJson.maxDictParams }}</div>

          <hr />
        </b-form-group>

        <b-form-group
          v-if="isTypeDict || isTypeMbDict"
          label="Maximum items to use per adapted parameter:"
          description="Select the maximum amount of items to use from the dictionary per adapted parameter"
        >
          <b-form-input
            id="max-dict-items"
            v-model="metaDataTuplesJson.maxDictItems"
            type="range"
            min="1"
            max="25"
            step="1"
          ></b-form-input>
          <div class="mt-2">Maximum items : {{ metaDataTuplesJson.maxDictItems }}</div>

          <hr />
        </b-form-group>

        <b-form-group
          v-if="sutsForSelection.length > 0"
          label="System under test:"
          description="Select system under test"
        >
          <b-form-select :options="sutsForSelection" v-model="project.sut.id" required>
            <template v-slot:first>
              <b-form-select-option :value="null" disabled>-- select a system under test --</b-form-select-option>
            </template>
          </b-form-select>
        </b-form-group>
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
const DEFAULT_META = {
  configuration: {},
  repetitions: 1,
  maxSequenceLength: 1,
  maxNumRequests: 1000,
  dictionaries: [],
  maxDictParams: 1,
  maxDictItems: 1,
  duration: 1,
  thresholdRequiredNonFuzzableMinimumRequests: 100,
  thresholdRequiredNonFuzzableValue: 100
};

export default {
  data() {
    return {
      project: {
        description: null,
        type: null,
        sut: {
          id: null
        },
        metaDataTuplesJson: null
      },
      configuration: null,
      dictionaries: [],
      metaDataTuplesJson: DEFAULT_META,
      types: [
        { value: "BASIC_FUZZER", text: "Basic" },
        { value: "DICTIONARY_FUZZER", text: "Dictionary" },
        { value: "MB_FUZZER", text: "ModelBased" },
        { value: "MB_BLACKBOX_FUZZER", text: "ModelBasedBlackbox" },
        { value: "MB_DICTIONARY_FUZZER", text: "ModelBasedDictionary" },
        { value: "MB_WHITEBOX_FUZZER", text: "ModelBasedWhitebox" }
      ]
    };
  },
  methods: {
    reset() {
      this.project.description = null;
      this.project.type = null;
      this.metaDataTuplesJson = DEFAULT_META;
    },
    cancel() {
      this.reset();
      this.$store.commit("set_project_display", { display: null });
    },
    setMetaDataTuplesJson() {
      this.metaDataTuplesJson.configuration = this.getConfigurationJson();

      this.metaDataTuplesJson.maxNumRequests = Number(
        this.metaDataTuplesJson.maxNumRequests
      );

      if (
        this.project.type === "BASIC_FUZZER" ||
        this.project.type === "DICTIONARY_FUZZER"
      ) {
        this.metaDataTuplesJson.repetitions = Number(
          this.metaDataTuplesJson.repetitions
        );
      } else {
        delete this.metaDataTuplesJson.repetitions;
      }

      if (
        this.project.type === "MB_FUZZER" || 
        this.project.type === "MB_BLACKBOX_FUZZER" ||
        this.project.type === "MB_WHITEBOX_FUZZER"
      ) {
        this.metaDataTuplesJson.maxSequenceLength = Number(
          this.metaDataTuplesJson.maxSequenceLength
        );
      } else {
        delete this.metaDataTuplesJson.maxSequenceLength;
      }

      if (
        this.project.type === "MB_BLACKBOX_FUZZER" ||
        this.project.type === "MB_WHITEBOX_FUZZER"
      ) {
        this.metaDataTuplesJson.duration = Number(
          this.metaDataTuplesJson.duration
        );
      } else {
        delete this.metaDataTuplesJson.duration;
      }

      if (
        this.project.type === "DICTIONARY_FUZZER" ||
        this.project.type === "MB_DICTIONARY_FUZZER"
      ) {
        this.metaDataTuplesJson.dictionaries = this.dictionaries;
      } else {
        delete this.metaDataTuplesJson.dictionaries;
      }

      if (
        this.project.type === "DICTIONARY_FUZZER" ||
        this.project.type === "MB_DICTIONARY_FUZZER"
      ) {
        this.metaDataTuplesJson.maxDictParams = Number(
          this.metaDataTuplesJson.maxDictParams
        );
        this.metaDataTuplesJson.maxDictItems = Number(
          this.metaDataTuplesJson.maxDictItems
        );
      } else {
        delete this.metaDataTuplesJson.maxDictParams;
        delete this.metaDataTuplesJson.maxDictItems;
      }

      this.project.metaDataTuplesJson = JSON.stringify(this.metaDataTuplesJson);
    },
    add() {
      this.setMetaDataTuplesJson();
      this.$store.dispatch("addProject", this.project).then(() => {
        this.cancel();
        this.$store.dispatch("findAllProjects");
      });
    },
    async findAllSuts() {
      if (this.$store.getters.suts.all.items === null) {
        await this.$store.dispatch("findAllSuts");
      }
    },
    async findAllConfigurations() {
      if (this.$store.getters.configurations.all.items === null) {
        await this.$store.dispatch("findAllConfigurations");
      }
    },
    async findAllDictionaries() {
      if (this.$store.getters.dictionaries.all.items === null) {
        await this.$store.dispatch("findAllDictionaries");
      }
    },
    getConfigurationJson() {
      let configurations = this.$store.getters.configurations.all.items.filter(
        config => {
          return this.configuration === config.id;
        }
      );

      let configurationsJson = {};

      if (configurations.length === 1) {
        configurationsJson = JSON.parse(configurations[0].itemsJson);
      }

      return configurationsJson;
    }
  },
  computed: {
    isTypeBasic() {
      return this.project.type && this.project.type === "BASIC_FUZZER";
    },
    isTypeMb() {
      return this.project.type && this.project.type === "MB_FUZZER";
    },
    isTypeMbBlackbox() {
      return this.project.type && this.project.type === "MB_BLACKBOX_FUZZER";
    },
    isTypeMbWhitebox() {
      return this.project.type && this.project.type === "MB_WHITEBOX_FUZZER";
    },
    isTypeMbDict() {
      return this.project.type && this.project.type === "MB_DICTIONARY_FUZZER";
    },
    isTypeDict() {
      return this.project.type && this.project.type === "DICTIONARY_FUZZER";
    },
    display() {
      return (
        this.$store.getters.projects.display !== null &&
        this.$store.getters.projects.display === "add"
      );
    },
    sutsForSelection() {
      this.findAllSuts();
      return this.$store.getters.sutsForSelection;
    },
    configurationsForSelection() {
      this.findAllConfigurations();
      return this.$store.getters.configurationsForSelection;
    },
    dictionariesForSelection() {
      this.findAllDictionaries();
      return this.$store.getters.dictionariesForSelection;
    }
  }
};
</script>