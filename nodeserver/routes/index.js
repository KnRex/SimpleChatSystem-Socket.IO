var socket = require('socket.io');
var express = require('express');
var http = require('http');
var router=express.Router();

var app = express();
var server = http.createServer(app);

var io = socket.listen(server);

var users=[];
var clients=[];


/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });

});

io.on('connection', function (client) {
  console.log("connected");
    client.on("add user",function(object) {
      var jsonObject=JSON.parse(object);
      if (!isUserAlreadyAdded(jsonObject)) {
        console.log(jsonObject)
        users.push(jsonObject);
        clients[jsonObject.userid]=client;
    }
  });

  client.on("disconnect",function(){
    var index=clients.indexOf(client)
    clients.splice(index,1);
    console.log("client disconnected");
  })

  client.on('SENDMESSAGE', function(data){
    var jsonData=JSON.parse(data)
    sendMessagetoClient(clients[jsonData.userid],jsonData.message)
  });




  client.on("GET_USERS",function(data){
    console.log(users)
    io.emit("GET_USER_LIST",users);
  });



});


function isUserAlreadyAdded(object){
  for(var user in users){
    if(users[user].userid===object.userid){
      return true;
    }
  }
  return false;

}

function sendMessagetoClient(client,message){
  client.emit("RECEIVE_MESSAGE",message);
}

server.listen(8000);

module.exports = router;
