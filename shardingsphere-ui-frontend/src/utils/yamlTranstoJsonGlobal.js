import yaml from 'js-yaml'

function Status(data) {
  Object.assign(this, data);
}

export const AuthYamlType = new yaml.Type('!AUTHORITY', {
  kind: 'mapping',
  instanceOf: Status,
  construct: data => new Status(data),
});

export const TranYamlType = new yaml.Type('!TRANSACTION', {
  kind: 'mapping',
  instanceOf: Status,
  construct: data => new Status(data),
});


export const SharYamlType = new yaml.Type('!SHARDING', {
  kind: 'mapping',
  instanceOf: Status,
  construct: data => new Status(data),
});


export const ReadYamlType = new yaml.Type('!READWRITE_SPLITTING', {
  kind: 'mapping',
  instanceOf: Status,
  construct: data => new Status(data),
});


export const EncYamlType = new yaml.Type('!ENCRYPT', {
  kind: 'mapping',
  instanceOf: Status,
  construct: data => new Status(data),
});

export const jsYaml = yaml


