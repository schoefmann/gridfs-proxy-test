var http = require('http'),
    mongo = require('mongodb'),
    url = require('url'),
    Server = mongo.Server,
    Db = mongo.Db,
    GridStore = mongo.GridStore;

var server = new Server('localhost', 27017, {auto_reconnect: true});
var db = new Db('test', server);

db.open(function(err, db) {
  var file, path;
  http.createServer(function (req, res) {
    path = url.parse(req.url).path.substring(1);
    file = new GridStore(db, path, 'r');
    file.open(function(err, file) {
      if (err || !file.length) {
        res.writeHead(404, {'Content-Type': 'text/plain'});
        res.end('Not Found\n');
      } else {
        res.writeHead(200, {
          'Content-Type': 'image/jpeg',
          'Etag': file.fileId,
          'Last-Modified': file.uploadDate.toGMTString()
        });
        var stream = file.stream(true);
        stream.on('data', function(data) {
          res.write(data);
        });
        stream.on('end', function() {
          res.end('');
        });
      }
    });
  }).listen(9999, "0.0.0.0");
});
