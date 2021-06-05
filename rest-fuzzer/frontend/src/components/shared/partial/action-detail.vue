<template>
  <div>
    <h6>Parameters:</h6>
    <div v-for="(parameter) in action.parameters" :key="getKey('prm', parameter)">
      <ParameterDetail :parameter="parameter"></ParameterDetail>
    </div>
    <div class="dl-div" v-if="action.parameters.length == 0">
      No parameters.
    </div>

    <hr />

    <h6>Responses:</h6>
    <div v-for="(response) in action.responses" :key="getKey('res', response)">
      <ActionResponseDetail :response="response"></ActionResponseDetail>
    </div>

    <hr />

    <h6>Dependencies:</h6>
    <div v-for="(dependency) in action.dependencies" :key="getKey('dep', dependency)">
      <dl class="row overview">
        <dt class="col-sm-2">identifier</dt>
        <dd class="col-sm-10">
          <b>#{{dependency.id}}</b> &nbsp;
          <b-badge variant="primary">{{dependency.discoveryModus}}</b-badge>
        </dd>
        <dt class="col-sm-2">parameter</dt>
        <dd class="col-sm-10">{{dependency.parameter | ppParameter}}</dd>
        <dt class="col-sm-2">depends on action</dt>
        <dd class="col-sm-10">{{dependency.actionDependsOn | ppAction}}</dd>
        <dt class="col-sm-2">from response</dt>
        <dd class="col-sm-10">{{dependency.parameterDependsOn}}</dd>
      </dl>
    </div>
    <div class="dl-div" v-if="action.dependencies.length == 0">
      No dependencies.
    </div>
  </div>
</template>

<script>
import ParameterDetail from "../../shared/partial/parameter-detail";
import ActionResponseDetail from "../../shared/partial/action-response-detail";

export default {
  props: ["action"],
  components: { ParameterDetail, ActionResponseDetail },
  methods: {
    getKey: function(prefix, object) {
      return `${prefix}_${object.id}`;
    }
  }
};
</script>