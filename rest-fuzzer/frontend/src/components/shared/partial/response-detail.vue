<template>
  <div>
    <h6>Information:</h6>
    <dl class="row overview">
      <dt class="col-sm-2">identifier</dt>
      <dd class="col-sm-10">
        <b>#{{response.id}}</b>
      </dd>
      <dt class="col-sm-2">code</dt>
      <dd class="col-sm-10">{{response.statusCode}}</dd>
      <dt class="col-sm-2">description</dt>
      <dd class="col-sm-10">{{response.statusDescription | nullFill }}</dd>
      <dt class="col-sm-2">exception</dt>
      <dd class="col-sm-10">{{ response.failureReason | nullFill}}</dd>
      <dt class="col-sm-2">body</dt>
      <dd class="col-sm-10">
        <div class="json" :inner-html.prop="response.body | json"></div>
      </dd>
      <dt class="col-sm-2">created @</dt>
      <dd class="col-sm-10">{{response.createdAt | date}}</dd>
    </dl>
  </div>
</template>

<script>
export default {
  props: ["response"]
};
</script>