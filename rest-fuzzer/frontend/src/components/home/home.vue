<template>
  <div>
    <b-jumbotron>
      <template v-slot:header>RESTFuzzer</template>
      <template v-slot:lead>
        Vulnerability detection in REST web services
      </template>
      <hr class="my-4">
      <p>
        This web application contains four types of fuzzers capable of fuzzing REST web services by deferring a model from an OAS v2 specification.
      </p>
      <div class="button-group-left">
        <b-button size="sm" variant="primary" href="/suts">Start</b-button>
        <b-button size="sm" variant="primary" href="/about">More information</b-button>
      </div>
    </b-jumbotron>
  </div>    
</template>
