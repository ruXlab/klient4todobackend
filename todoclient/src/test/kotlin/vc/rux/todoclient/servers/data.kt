package vc.rux.todoclient.servers

internal val sampleYaml = """
    "ThingA":
      description:
        descrA A A A A
      sourcecode_url: https://aaa.com/
      live_url: https://aaa.server.com/
      tags:
        - kotlin
        - java

    "Thing B":
      description:
        description B B B 
        multi line text
      sourcecode_url: https://bbb.com/
      live_url: https://bbb.server.com/v1/todos
      tags:
        - msdos
        - emm386
        - keyb
""".trimIndent()