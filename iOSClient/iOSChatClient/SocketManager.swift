//
//  SocketManager.swift
//  iOSChatClient
//
//  Created by Karthikeyan Gopal on 17/02/16.
//  Copyright © 2016 Karthikeyan Gopal. All rights reserved.
//

import Foundation

class socketManager{
    
    static let instance = socketManager();
    var socket:SocketIOClient?;
    
    private init(){
    }
    
    func connect(){
       socket =  SocketIOClient(socketURL:
          NSURL(string: "http://192.168.2.7:8000")!, options: [.Log(true), .ForcePolling(true)])
        socket?.connect()
    }
    
    
    func getSocket()->SocketIOClient{
        return socket!;
    }
    
    func onConnection(completionHandler : (() -> Void)){
            socket!.on("connect") {data, ack in
            print("socket connected")
            completionHandler()
        }
    }
    
    func onErrorInConnection(completionHandler : (()-> Void)){
        socket!.on("connect_error"){data, ack in
        print("Error in connection")
        completionHandler()
        }

    }
    
    func onEventHandler(eventName:String, completionHandler :((response: AnyObject)-> Void)){
        socket!.on(eventName) { (data, ack) -> Void in
            print("event triggered")
            completionHandler(response: data);
        }
    }
    
    func removeOnConnectionCallback(){
        socket!.off("connect")
    }
    
    func removeOnErrorCallback(){
        socket!.off("connect_error")
    }
    
    func removeEventCallback(name: String){
        socket!.off(name);
    }
    
    
}