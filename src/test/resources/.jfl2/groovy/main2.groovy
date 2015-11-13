package bar

class Foo {
    def name

    def getName() {
        "main2 : " + name
    }
}

println new Foo(name: "Hello groovy 2").getName()
println "var: " + varstr
