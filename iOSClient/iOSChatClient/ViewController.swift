//
//  ViewController.swift
//  iOSChatClient
//
//  Created by Karthikeyan Gopal on 17/02/16.
//  Copyright © 2016 Karthikeyan Gopal. All rights reserved.
//

import UIKit

class ViewController: UIViewController {
    
    
    @IBOutlet weak var usernameField: UITextField!
    
    @IBOutlet weak var connectBtn: UIButton!

    @IBAction func onConnectClicked(sender: AnyObject) {
        let name=usernameField.text
        if name!.isEmpty{
            
        }
        else{
             let instance = socketManager.instance
            let callback =  {
            
                let userID:String=UIDevice.currentDevice().identifierForVendor!.UUIDString;
                let dictionary:[String:String] = ["userid":userID, "username":name!]
                do {
                    let jsonData = try NSJSONSerialization.dataWithJSONObject(dictionary, options: NSJSONWritingOptions(rawValue: 0))
                   
                    instance.getSocket().emit("add user", jsonData)
                    if let resultController = self.storyboard!.instantiateViewControllerWithIdentifier("USERLISTVC") as? UserListVC {
                        
                        self.navigationController?.pushViewController(resultController, animated: false)
                    }
                    
                } catch let error as NSError {
                    print(error)
                }
                }
            
            instance.connect()
            instance.onConnection(callback)
        }
        
    }
    

    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.title = "Chat Client"
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }


}

