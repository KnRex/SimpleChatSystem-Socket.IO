//
//  ChatVC.swift
//  iOSChatClient
//
//  Created by Karthikeyan Gopal on 23/02/16.
//  Copyright © 2016 Karthikeyan Gopal. All rights reserved.
//

import UIKit

class ChatVC: UIViewController {

    @IBOutlet weak var chatText: UITextField!
    
    @IBOutlet weak var sendBtn: UIButton!
    
    @IBOutlet weak var tableView: UITableView!
    var username:String?
    var userId:String?
    
    let mSocketManger = socketManager.instance
    
    var socket:SocketIOClient?
    var messageList = [String]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.title = username

        socket = mSocketManger.getSocket()
        mSocketManger.onEventHandler("RECEIVE_MESSAGE") { (response) -> Void in
            
            print(response)
            
            print(" class type \(response.dynamicType)")
            
            if let message = response as? NSArray {
                self.messageList.append(message[0] as! String)
                self.tableView.reloadData()
            }

        }

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    @IBAction func onSendClicked(sender: AnyObject) {
        
        if chatText.text?.isEmpty==false{
            let dictionary:[String:String] = ["message":chatText.text!, "userid":userId!]
            do {
                let jsonData = try NSJSONSerialization.dataWithJSONObject(dictionary, options: NSJSONWritingOptions(rawValue: 0))
                socket!.emit("SENDMESSAGE", jsonData)

                self.messageList.append(chatText.text!)
                self.tableView.reloadData()
                chatText.text? = ""
                
            } catch let error as NSError {
                print(error)
            }
        }
    
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return messageList.count
        
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell
        
    {
        
        let cell:UITableViewCell=UITableViewCell(style: UITableViewCellStyle.Subtitle, reuseIdentifier: "mycell")
        cell.textLabel!.text = messageList[indexPath.row]
        return cell
        
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        
        
    }

    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
