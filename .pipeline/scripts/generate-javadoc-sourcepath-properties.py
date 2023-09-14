import json


def write_javadoc_sourcepath_properties():
    with open("module-inventory.json", "r") as file:
        module_inventory = json.load(file)

        with open("javadoc.properties", "w") as output:
            output.write("javadoc.sourcepath = ")

            for i, module in enumerate(module_inventory, start=1):
                if module["releaseAudience"] != "Public":
                    continue
                target_path = module["pomFile"].replace("pom.xml", "target/delombok;")
                if i == len(module_inventory):
                    output.write(target_path)
                else:
                    output.write(target_path + "\\\n")


def main():
    write_javadoc_sourcepath_properties()


if __name__ == '__main__':
    main()
