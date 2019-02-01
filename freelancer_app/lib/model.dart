

class  JobEntry {
  String title;
  String description;
  Uri href;
  String salary;
  String source;

  // Constructor, with syntactic sugar for assignment to members.
  JobEntry(this.title, this.description,this.href, this.salary,this.source) {
    title = this.title;
    description = this.description;
    href = this.href;
    salary = this.salary;
    source = this.source;
  }
}