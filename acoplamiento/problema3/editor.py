class Editor:
    def check_spelling(self, text):
        errors = self.check(text)
        if errors:
            self.display_errors(errors)
        else:
            self.display_success_message()

    def display_errors(self, errors):
        for error in errors:
            print("ERROR: {}".format(error))
        return None

    def display_success_message(self):
        print("No errors found!")
        return None

    def check(self, text):
        words = text.split()
        errors = []
        for word in words:
            if word not in ['foo', 'bar']:
                errors.append(word)
        return errors

    def runEditor(self):
        print("Running editor...")
        print("Enter text:")
        text = input()
        self.check_spelling(text)

if __name__ == "__main__":
    editor = Editor()
    editor.runEditor()