import 'package:flutter/material.dart';
import 'dart:io';
import 'dart:convert';
import 'package:tuple/tuple.dart';
import 'package:url_launcher/url_launcher.dart';

void main() => runApp(MaterialApp(
      title: 'Freelance job search',
      home: HomeScreen(),
    ));

class HomeScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Select a programming language'),
      ),
      body: Center(
        child: RaisedButton(
          child: Text('View Results'),
          onPressed: () {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => MyApp()),
            );
          },
        ),
      ),
    );
  }
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Job listings"),
      ),
      body: MaterialApp(
        title: 'Freelancer jobs',
        home: JobList(),
      ),
    );

//
//  @override
//  Widget build(BuildContext context) {
//    return MaterialApp(
//      title: 'Freelancer jobs',
//      home: JobList(),
//    );
//  }
  }
}

class JobListState extends State<JobList> {
  final _suggestions = <Tuple2<String, Uri>>[];

  final _biggerFont = const TextStyle(fontSize: 18.0);

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

  Widget _buildRow(Tuple2<String, Uri> job) {
    return ListTile(
      title: Text(
        job.item1,
        style: _biggerFont,
      ),
      onTap: () => onTapped(job.item2),
    );
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
        .getUrl(Uri.parse("http://10.0.2.2:8080/jobs/stream"))
        .then((HttpClientRequest request) {
      return request.close();
    }).then((HttpClientResponse response) {
      response.forEach((charCodes) {
        var rawString = new String.fromCharCodes(charCodes);
        var object = json.decode(rawString);
        setState(() {
          _suggestions
              .add(Tuple2(object["description"], Uri.parse(object["href"])));
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
  @override
  JobListState createState() => new JobListState();
}
