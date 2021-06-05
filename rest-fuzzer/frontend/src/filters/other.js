import Vue from 'vue'

Vue.filter('downCase', function (value, filterFunction) {
  return filterFunction(value);
});

Vue.filter('dynamicFilter', function (value, filterFunction) {
  return filterFunction(value);
});

Vue.filter('downCase', function (string) {
  return string.toLowerCase();
});

Vue.filter('enumToHuman', function (string) {
  string = string.split("_").map(
    s => {
      return s.toLowerCase();
    }
  ).join(" ");

  string = string[0].toUpperCase() + string.substring(1).toLowerCase();;

  return string;
});

Vue.filter('toFilename', function (string) {
  string = string.split(" ").map(
    s => {
      return s.toLowerCase();
    }
  ).join("_");

  return string;
});


Vue.filter('json', function (json) {
  json = JSON.stringify(JSON.parse(json), null, 2);

  // keys
  json = json.replace(/"[\w]*":/g, function (match) {
    return '<span class="key">' + match.substring(1, match.length - 2) + '</span>:';
  });

  // strings
  json = json.replace(/ "[\w]*"/g, function (match) {
    return ' "<span class="string">' + match.substring(2, match.length - 1) + '</span>"';
  });

  // numbers
  json = json.replace(/ ([0-9]*[.])?[0-9]+/g, function (match) {
    return ' <span class="number">' + match.substring(1, match.length) + '</span>';
  });

  // booleans (true)
  json = json.replace(/ (true)/g, function (match) {
    return ' <span class="true">' + match.substring(1, match.length) + '</span>';
  });

  // booleans (false)
  json = json.replace(/ (false)/g, function (match) {
    return ' <span class="false">' + match.substring(1, match.length) + '</span>';
  });

  return json;
});

Vue.filter('ppAction', function (action) {
  if (!action) {
    return '-';
  }
  return `${action.path} [${action.httpMethod}]`;
});

Vue.filter('ppParameter', function (parameter) {
  if (!parameter) {
    return '-';
  }
  return `${parameter.name} [${parameter.type}]`;
});

Vue.filter('nullFill', function (object, nullReplace = '-') {
  return object === null ? nullReplace : object;
});