<template>
  <div>
    <h6>Information:</h6>
    <dl class="row overview">
      <dt class="col-sm-2">identifier</dt>
      <dd class="col-sm-10">
        <b>#{{request.id}}</b>
      </dd>
      <dt v-if="request.headerParametersJson !== '{}'"  class="col-sm-2">header parameters</dt>
      <dd v-if="request.headerParametersJson !== '{}'"  class="col-sm-10">
        <div class="json" :inner-html.prop="request.headerParametersJson | json"></div>
      </dd>
      <dt v-if="request.pathParametersJson !== '{}'" class="col-sm-2">path parameters</dt>
      <dd v-if="request.pathParametersJson !== '{}'" class="col-sm-10">
        <div class="json" :inner-html.prop="request.pathParametersJson | json"></div>
      </dd>
      <dt v-if="request.formdataParametersJson !== '{}'"  class="col-sm-2">form data parameters</dt>
      <dd v-if="request.formdataParametersJson !== '{}'"  class="col-sm-10">
        <div class="json" :inner-html.prop="request.formdataParametersJson | json"></div>
      </dd>
      <dt v-if="request.queryParametersJson !== '{}'"  class="col-sm-2">query parameters</dt>
      <dd v-if="request.queryParametersJson !== '{}'"  class="col-sm-10">
        <div class="json" :inner-html.prop="request.queryParametersJson | json"></div>
      </dd>
      <dt class="col-sm-2">sequence</dt>
      <dd class="col-sm-10">
        <b>#{{request.sequence ? request.sequence.id : '-'}}</b>
      </dd>
      <dt class="col-sm-2">created @</dt>
      <dd class="col-sm-10">{{request.createdAt | date }}</dd>
    </dl>
  </div>
</template>

<script>
export default {
  props: ["request"]
};
</script>

<style scoped>
.param-block {
  width: 250px;
}
</style>