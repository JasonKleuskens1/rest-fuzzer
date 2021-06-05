  <template>
  <b-card v-if="task !== null" header-tag="header">
    <template v-slot:header>
      <b-icon icon="eye" font-scale="1"></b-icon>&nbsp;Detail task
    </template>
    <b-card-text>
      <b-tabs nav-tabs card>
        <b-tab title="Information" active>
          <div class="row">
            <div class="col">
              <div class="button-group-left">
                <b-button
                  v-if="hasRouteFor('sut')"
                  size="sm"
                  variant="primary"
                  title="go to SUT"
                  :to="getRouteFor('sut')"
                >
                  <b-icon icon="link45deg" font-scale="1"></b-icon>&nbsp;
                  go to SUT
                </b-button>
                <b-button
                  v-if="hasRouteFor('project')"
                  size="sm"
                  variant="primary"
                  title="go to project"
                  :to="getRouteFor('project')"
                >
                  <b-icon icon="link45deg" font-scale="1"></b-icon>&nbsp;
                  go to project
                </b-button>
                <b-button
                  v-if="hasRouteFor('report')"
                  size="sm"
                  variant="primary"
                  title="go to report"
                  :to="getRouteFor('report')"
                >
                  <b-icon icon="link45deg" font-scale="1"></b-icon>&nbsp;
                  go to report
                </b-button>

                <b-button
                  size="sm"
                  v-b-modal.task-delete
                  variant="outline-danger"
                  title="delete this task"
                >
                  <b-icon icon="trash" font-scale="1"></b-icon>&nbsp;delete
                </b-button>
              </div>
            </div>
          </div>
          <div class="row">
            <div class="col">
              <dl class="dl-horizontal">
                <dt>Identifier</dt>
                <dd>{{task.id}}</dd>
                <dt>Name</dt>
                <dd>{{task.name}}</dd>
                <dt>Canonical name</dt>
                <dd>{{task.canonicalName}}</dd>
                <dt>Progress</dt>
                <dd>
                  <b-progress
                    :value="task.progress"
                    :max="100"
                    height="1.5em"
                    style="margin-top:2px; border:1px solid #ffffff;"
                  ></b-progress>
                </dd>
                <dt>Status</dt>
                <dd>
                  <b-icon
                    v-if="task.status === constants.TASK_STATUS_QUEUED"
                    icon="clock"
                    variant="success"
                    font-scale="1"
                  ></b-icon>
                  <b-spinner
                    v-if="task.status === constants.TASK_STATUS_RUNNING"
                    small
                    variant="primary"
                    type="grow"
                  ></b-spinner>
                  <b-icon
                    v-if="task.status === constants.TASK_STATUS_CRASHED"
                    icon="exclamation-circle-fill"
                    variant="danger"
                    font-scale="1"
                  ></b-icon>
                  <b-icon
                    v-if="task.status === constants.TASK_STATUS_FINISHED"
                    icon="check-circle"
                    variant="success"
                    font-scale="1"
                  ></b-icon>
                  ({{task.status | downCase }})
                </dd>
                <dt>Started @</dt>
                <dd>{{task.startedAt | date }}</dd>
                <dt>Ended @</dt>
                <dd>{{task.endedAt | date }}</dd>
              </dl>
            </div>
            <div class="col">
              <dl class="dl-horizontal">
                <dt>Meta data:</dt>
                <dd>
                  <div class="json" :inner-html.prop="task.metaDataTuplesJson | json"></div>
                </dd>
              </dl>
            </div>
          </div>
        </b-tab>
      </b-tabs>
    </b-card-text>
  </b-card>
</template>

<script>
import Constants from "../../shared/constants";

export default {
  components: { Constants },
  data() {
    return {
      constants: Constants
    };
  },
  computed: {
    display() {
      return this.task !== null;
    },
    task() {
      return this.$store.getters.tasks.current.item;
    }
  },
  methods: {
    refreshTask() {
      if (this.display) {
        this.$store
          .dispatch("findTask", this.$store.getters.tasks.current.item.id)
          .catch(error => {
            this.$timer.stop("refreshTask");
          });
      }
    },
    hasRouteFor(identifier) {
      return this.task.metaDataTuplesJson.indexOf(`${identifier}_id`) !== -1;
    },
    getRouteFor(identifier) {
      const jsonObject = JSON.parse(this.task.metaDataTuplesJson);
      const id = jsonObject[`${identifier}_id`];
      return { name: identifier, params: { id: id } };
    }
  },
  timers: {
    refreshTask: {
      time: Constants.REFRESH_TIMEOUT,
      autostart: true,
      repeat: true
    }
  }
};
</script>