Todo servers client SDK
=========

This is a platform-independent JVM library for communication with TodoServers consts of:

- `ServerListApi` - components which retrieves and parses list of available todo servers from 
[implementations.yaml](https://raw.githubusercontent.com/TodoBackend/todo-backend-site/master/data/implementations.yaml)
published on [github](https://github.com/TodoBackend/todo-backend-site/tree/master/data)
- `TodoClient` - REST client itself, it has complete set of features implemented

### Dependencies

- **kotlin** as an expression language
- **okhttp3** and **retrofit** as a REST clients
- **snakeyaml** and **moshi** for yaml and json parsing 
