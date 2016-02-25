//
//  UserListVC.swift
//  iOSChatClient
//
//  Created by Karthikeyan Gopal on 22/02/16.
//  Copyright © 2016 Karthikeyan Gopal. All rights reserved.
//

import UIKit

class UserListVC: UIViewController {

    @IBOutlet weak var tableView: UITableView!
    
    var userList=[String]()
    var userIdList=[String]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.title = "Users"

        let mSocketManager = socketManager.instance
        
       
        mSocketManager.onEventHandler("GET_USER_LIST") { (response) -> Void in

             let userID:String=UIDevice.currentDevice().identifierForVendor!.UUIDString;
                if let users = response as? [AnyObject] {
                    for var i=0;i<users.count;i++ {
                        let user = users[i] as! NSArray
                        for var j=0;j<user.count;j++ {
                            
                            if let u = user[j] as? [String:String] {
                               
                                if(u["userid"]! != userID) {
                                self.userList.append(u["username"]!)
                                self.userIdList.append(u["userid"]!)
                                }
                            }
                            
                            
                        }
                        
                    }
                    
                }
            self.tableView.reloadData()

        }
        
         mSocketManager.getSocket().emit("GET_USERS", "userslist")

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
            }
    

    
    
    
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return userList.count
      
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell
        
    {
        
        let cell:UITableViewCell=UITableViewCell(style: UITableViewCellStyle.Subtitle, reuseIdentifier: "mycell")
        cell.textLabel!.text = userList[indexPath.row]
        return cell
        
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        if let resultController = self.storyboard!.instantiateViewControllerWithIdentifier("ChatVC") as? ChatVC {
            
            resultController.username = self.userList[indexPath.row]
            resultController.userId = self.userIdList[indexPath.row]
            self.navigationController?.pushViewController(resultController, animated: false)

        }
        
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
