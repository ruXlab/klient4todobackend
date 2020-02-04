package vc.rux.todoclient.todoclient

val TODOS_ALL = """
    [
      {
        "id": 42,
        "title": "use kotlin",
        "completed": true,
        "order": 0,
        "url": "https://todo-backend-hanami.herokuapp.com/todos/42"
      },
      {
        "id": 777,
        "title": "Implement UI",
        "completed": false,
        "order": 777,
        "url": "https://todo-backend-hanami.herokuapp.com/todos/777"
      }
    ]
""".trimIndent()

val TODOS_SINGLE = """
      {
        "id": 42,
        "title": "todo title",
        "completed": true,
        "order": 146,
        "url": "https://todo-backend-hanami.herokuapp.com/todos/42"
      }
""".trimIndent()