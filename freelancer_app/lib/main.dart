import 'package:flutter/material.dart';
import 'dart:io';
import 'dart:convert';
import 'dart:async';
import 'package:tuple/tuple.dart';
import 'package:url_launcher/url_launcher.dart';
import 'model.dart';

void main() => runApp(MaterialApp(
      title: 'Freelance job search',
      home: HomeScreen(),
    ));

class LanguagesWidget extends StatefulWidget {
  LanguagesWidget({Key key}) : super(key: key);

  @override
  _LanguagesWidgetState createState() => new _LanguagesWidgetState();
}

class _LanguagesWidgetState extends State<LanguagesWidget> {
  List<DropdownMenuItem<String>> _dropDownMenuItems = [
    new DropdownMenuItem(value: "y", child: new Text("y"))
  ];
  String _currentLanguage;

  @override
  void initState() {
    super.initState();
    setDropDownMenuItems();
  }

  void setDropDownMenuItems() {
    HttpClient client = new HttpClient();
    client
        .getUrl(Uri.parse("http://10.0.2.2:8080/languages"))
        .then((HttpClientRequest request) {
      return request.close();
    }).then((HttpClientResponse response) {
      response.forEach((charCodes) {
        var rawString = new String.fromCharCodes(charCodes);
        var object = json.decode(rawString);
        setState(() {
          List<DropdownMenuItem<String>> items = new List();
          for (String lang in object) {
            setState(() {
              items.add(
                  new DropdownMenuItem(value: lang, child: new Text(lang)));
            });
          }
          _dropDownMenuItems = items;
        });
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return new Container(
      color: Colors.white,
      child: new Center(
          child: new Column(
        crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          new Text("Please choose your language: "),
          new Container(
            padding: new EdgeInsets.all(16.0),
          ),
          new DropdownButton(
            value: _currentLanguage,
            items: _dropDownMenuItems,
            onChanged: changedDropDownItem,
          ),
          RaisedButton(
            child: Text('View Results'),
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                    builder: (context) => MyApp(language: _currentLanguage)),
              );
            },
          )
        ],
      )),
    );
  }

  void changedDropDownItem(String selectedLanguage) {
    setState(() {
      _currentLanguage = selectedLanguage;
    });
  }
}

class HomeScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Programming Jobs list'),
      ),
      body: ListView(children: [
        LanguagesWidget(),
      ]),
    );
  }
}

class MyApp extends StatelessWidget {
  final String language;

  MyApp({Key key, @required this.language}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Job listings"),
      ),
      body: MaterialApp(
        title: 'Freelancer jobs',
        home: JobList(language: language),
      ),
    );
  }
}

class JobListState extends State<JobList> {
  final String language;

  JobListState({@required this.language}) : super();

  final List<JobEntry> _suggestions = new List();

  final _titleFont = const TextStyle(fontSize: 16.0);
  final _subtitleFont = const TextStyle(fontSize: 12.0);

  Widget _buildSuggestions() {
    return ListView.builder(
        padding: const EdgeInsets.all(16.0),
        itemCount: _suggestions.length * 2,
        itemBuilder: (context, doubledItemCount) {
          if (doubledItemCount.isOdd) return Divider();
          var currentIndex = doubledItemCount ~/ 2;

          return _buildRow(_suggestions[currentIndex]);
        });
  }

  Widget _buildRow(JobEntry job) {
    var title = "${job.title} ${job.salary}";
    return ListTile(
      title: Text(
        title,
        style: _titleFont,
      ),
      subtitle: Text(
        job.description,
        style: _subtitleFont,
      ),
      onLongPress: () => onHoldButton(title,job.description),
      onTap: () => onTapped(job.href),
    );
  }

  GestureLongPressCallback onHoldButton(String title, String description) {
    showDialog(
        context: context,
        child: new AlertDialog(
          title: new Text(title),
          content: new Text(description),
        ));
  }

  void onTapped(Uri href) {
    _launchURL(href);
  }

  _launchURL(Uri jobLink) async {
    if (await canLaunch(jobLink.toString())) {
      await launch(jobLink.toString());
    } else {
      throw 'Could not launch ${jobLink.toString()}';
    }
  }

  @override
  void initState() {
    super.initState();
    fetchJobListings();
  }

  void fetchJobListings() {
    HttpClient client = new HttpClient();
    client
        .getUrl(Uri.parse(
            "http://10.0.2.2:8080/jobs/language/${Uri.encodeComponent(language.toLowerCase())}"))
        .then((HttpClientRequest request) {
      return request.close();
    }).then((HttpClientResponse response) {
      response.transform(utf8.decoder).listen((contents) {
        var object = json.decode(contents);
        setState(() {
          _suggestions.add(JobEntry(object["title"], object["description"],
              Uri.parse(object["href"]), object["salary"], object["source"]));
        });
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _buildSuggestions(),
    );
  }
}

class JobList extends StatefulWidget {
  final String language;

  JobList({Key key, @required this.language}) : super(key: key);

  @override
  JobListState createState() => new JobListState(language: language);
}
