package test.groovy


class Foo {
    def name

    def getName() {
        "main : " + name
    }
}

println new Foo(name: "Hello groovy").getName()
