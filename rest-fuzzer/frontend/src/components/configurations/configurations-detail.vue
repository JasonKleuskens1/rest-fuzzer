<template>
  <b-card v-if="this.configuration" header-tag="header">
    <template v-slot:header>
      <b-icon icon="eye" font-scale="1"></b-icon>&nbsp;Detail configuration
    </template>
    <b-card-text>
      <b-tabs nav-tabs card>
        <b-tab title="Information" active>
          <div class="row">
            <div class="col">
              <div class="button-group-left">
                <b-button
                  size="sm"
                  type="submit"
                  v-b-modal.configuration-delete
                  variant="outline-danger"
                  title="delete this configuration"
                >
                  <b-icon icon="trash" font-scale="1"></b-icon>&nbsp;delete
                </b-button>

                <b-button
                  size="sm"
                  type="submit"
                  variant="primary"
                  title="copy configuration to clipboard"
                  @click="copy"
                >
                  <b-icon icon="clipboard" font-scale="1"></b-icon>&nbsp;copy json to clipboard
                </b-button>
              </div>
            </div>
          </div>
          <div class="row">
            <div class="col">
              <dl class="dl-horizontal">
                <dt>Identifier</dt>
                <dd>{{this.configuration.id}}</dd>
                <dt>Name</dt>
                <dd>{{this.configuration.name ? this.configuration.name : '-'}}</dd>
                <dt>Created @</dt>
                <dd>{{this.configuration.createdAt | date }}</dd>
              </dl>
            </div>
            <div class="col">
              <dl class="dl-horizontal">
                <dt>Configuration</dt>
                <dd class="json fixed" :inner-html.prop="this.configuration.itemsJson | json"></dd>
              </dl>
            </div>
          </div>
        </b-tab>
      </b-tabs>
    </b-card-text>
  </b-card>
</template>

<script>
export default {
  data() {
    return {};
  },
  methods: {
    copy() {
      this.$copyText(
        JSON.stringify(JSON.parse(this.configuration.itemsJson), null, 2)
      ).then(
        function(e) {
          // succes
        },
        function(e) {
          // fail
        }
      );
    }
  },
  computed: {
    configuration() {
      return this.$store.getters.configurations.current.item;
    }
  }
};
</script>