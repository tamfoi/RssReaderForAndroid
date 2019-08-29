//
//  AddRssUrlViewController.swift
//  RssReaderForiOS
//
//  Created by stf01430 on 2019/07/09.
//  Copyright © 2019 tamfoi. All rights reserved.
//

import UIKit
import RealmSwift

class AddRssUrlViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var addRssUrlTextField: UITextField!
    @IBOutlet weak var rssUrlTableView: UITableView!
    
    @IBAction func addRssUrl(_ sender: Any) {
        
        try! realm.write {
            
            let rssUrl = RssUrl()
            rssUrl.url = addRssUrlTextField.text!
            
            let allRssUrl = self.realm.objects(RssUrl.self)
            if allRssUrl.count != 0 {
                rssUrl.id = allRssUrl.max(ofProperty: "id")! + 1
            }
            
            self.realm.add(rssUrl, update: true)
        
        }
        
        rssUrlTableView.reloadData()
        
    }
    
    // Realmインスタンスを取得する
    let realm = try! Realm()
    
    var rssUrlArray = try! Realm().objects(RssUrl.self).sorted(byKeyPath: "id", ascending: false)
    
    override func viewDidLoad() {
        super.viewDidLoad()

        rssUrlTableView.delegate = self
        rssUrlTableView.dataSource = self
    }
    

    // MARK: UITableViewDataSourceプロトコルのメソッド
    // データの数（＝セルの数）を返すメソッド
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return rssUrlArray.count
    }
    
    // 各セルの内容を返すメソッド
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        // 再利用可能な cell を得る
        let cell = rssUrlTableView.dequeueReusableCell(withIdentifier: "rssUrlCell", for: indexPath)
        
        let rssUrl = rssUrlArray[indexPath.row]
        cell.textLabel?.text = rssUrl.url
        
        return cell
    }
    
    // MARK: UITableViewDelegateプロトコルのメソッド
    // 各セルを選択した時に実行されるメソッド
    /*func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    }*/
    
    // セルが削除が可能なことを伝えるメソッド
    func tableView(_ tableView: UITableView, editingStyleForRowAt indexPath: IndexPath)-> UITableViewCell.EditingStyle {
        return .delete
    }
    
    // Delete ボタンが押された時に呼ばれるメソッド
    func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {
    
        if editingStyle == .delete {
            // データベースから削除する
            try! realm.write {
                self.realm.delete(self.rssUrlArray[indexPath.row])
                rssUrlTableView.deleteRows(at: [indexPath], with: .fade)
            }
        }
    
    }
}
