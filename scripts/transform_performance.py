import sys
from os import listdir


class Transformer:
    """
    Consolidates raw performance benchmark results into one CSV file suitable for processing in R

    The transformation includes generating nicer names for providers (e.g., alibaba to AliBaba) and operation names
    (e.g., create to OP1 - Create)

    CLI parameters are: directory containing the result files and target file
    """
    PROVIDERS = {"alibaba": "AliBaba",
                 "empire": "Empire",
                 "jopa": "JOPA",
                 "komma": "KOMMA",
                 "rdfbeans": "RDFBeans"}
    OPERATIONS = {"create": "OP1 - Create",
                  "create-batch": "OP2 - Batch create",
                  "retrieve": "OP3 - Retrieve",
                  "retrieve-all": "OP4 - Retrieve all",
                  "update": "OP5 - Update",
                  "delete": "OP6 - Delete"
                  }

    def __init__(self, directory, target):
        self.directory = directory
        self.target = target

    def transform(self):
        out = open(self.target, 'w')
        out.write('operation,provider,time,time_s\n')
        out.close()
        files = listdir(self.directory)
        files.sort()
        for file in files:
            self.transform_file(file)
        print("Data written into file " + str(self.target))

    def transform_file(self, filename):
        path = str(self.directory)
        if not path.endswith('/'):
            path += '/'
        file = open(path + filename, 'r')
        print("Loading data from file " + str(file.name))
        out = open(self.target, 'a')
        operation = Transformer.resolve_operation_name(filename[filename.find('_') + 1:filename.find('.')])
        provider = Transformer.resolve_provider_name(filename[0:filename.find('-')])
        i = 0
        for line in file:
            i = i + 1
            time_ms = float(line)
            time_s = round(time_ms / 1000, 3)
            out.write('{},{},{},{}\n'.format(operation, provider, str(time_ms), str(time_s)))
        out.close()

    @staticmethod
    def resolve_operation_name(operation):
        return Transformer.OPERATIONS.get(operation)

    @staticmethod
    def resolve_provider_name(provider):
        return Transformer.PROVIDERS.get(provider)


if __name__ == "__main__":
    directory = sys.argv[1]
    target = sys.argv[2]
    transformer = Transformer(directory, target)
    transformer.transform()
