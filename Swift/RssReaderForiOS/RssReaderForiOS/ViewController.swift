//
//  ViewController.swift
//  RssReaderForiOS
//
//  Created by stf01430 on 2019/07/09.
//  Copyright © 2019 tamfoi. All rights reserved.
//

import UIKit
import RealmSwift
import Alamofire

class ViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UIPickerViewDelegate, UIPickerViewDataSource, XMLParserDelegate {

    @IBOutlet weak var rssUrlPickerView: UIPickerView!
    @IBOutlet weak var articleTableView: UITableView!
    
    // Realmインスタンスを取得する
    let realm = try! Realm()
    
    var rssUrlArray = try! Realm().objects(RssUrl.self).sorted(byKeyPath: "id", ascending: false)
    
    var rssType = ""
    var articleTitle = ""
    var articleLink = ""
    var isItemTag = false
    var isTitleTag = false
    var isLinkTag = false
    
    var request: Alamofire.Request? = nil
    
    var result = [Dictionary<String, String>]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        rssUrlPickerView.delegate = self
        rssUrlPickerView.dataSource = self
        
        articleTableView.delegate = self
        articleTableView.dataSource = self
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        rssUrlPickerView.reloadAllComponents()

        if(rssUrlArray.count > rssUrlPickerView.selectedRow(inComponent: 0)) {
            loadArticle(url: rssUrlArray[rssUrlPickerView.selectedRow(inComponent: 0)].url)
        } else {
            self.articleTableView.reloadData()
        }
    }
    
    func loadArticle(url: String) {
        
        if(self.request != nil){
            self.request?.cancel()
        }
        
        self.request = Alamofire.request(url).responseString { response in
            switch response.result {
                case .success:
                    
                    let xmlParser = XMLParser(data: (response.result.value?.data(using: .utf8))!)
                
                    xmlParser.delegate = self;
                    xmlParser.parse()
                
                case .failure(let error):
                    print(error)
                    self.clearLoadArticleState()
                    self.articleTableView.reloadData()
            }
        }
    
    }
    
    func clearLoadArticleState() {
        self.rssType = ""
        self.articleTitle = ""
        self.articleLink = ""
        self.isItemTag = false
        self.isTitleTag = false
        self.isLinkTag = false
        self.result = [Dictionary<String, String>]()
    }
    
    // XML解析開始時に実行されるメソッド
    func parserDidStartDocument(_ parser: XMLParser) {
        
        clearLoadArticleState()
        self.articleTableView.reloadData()
        
    }
    
    // 解析中に要素の開始タグがあったときに実行されるメソッド
    func parser(_ parser: XMLParser, didStartElement elementName: String, namespaceURI: String?, qualifiedName qName: String?, attributes attributeDict: [String : String]) {
        //print("開始タグ:" + elementName)
        
        if(elementName == "rss") {
            self.rssType = "rss"
            //print(self.rssType)
        } else if(elementName == "feed") {
            self.rssType = "atom"
            //print(self.rssType)
        }
        
        //記事アイテムのタグに到達したらフラグを立てる
        if((rssType == "rss" && elementName == "item") || (rssType == "atom" && elementName == "entry")){
            self.isItemTag = true
            //print(elementName)
        }
        
        if(self.isItemTag && elementName == "title") {
            self.isTitleTag = true
            //print(elementName)
        }
        
        if(self.isItemTag && elementName == "link") {
            self.isLinkTag = true
            
            if(self.rssType == "atom"){
                self.articleLink = attributeDict["href"]!
            }
        }
    }
    
    // 開始タグと終了タグでくくられたデータがあったときに実行されるメソッド
    func parser(_ parser: XMLParser, foundCharacters string: String) {
        
        if(self.isTitleTag){
            articleTitle = articleTitle + string
        }
        if(self.isLinkTag && self.rssType == "rss"){
            articleLink = string
        }
    }
    
    // 解析中に要素の終了タグがあったときに実行されるメソッド
    func parser(_ parser: XMLParser, didEndElement elementName: String, namespaceURI: String?, qualifiedName qName: String?) {
        //print("終了タグ:" + elementName)
        
        //記事アイテムのタグの終端まで到達したらフラグを下げる
        if((self.rssType == "rss" && elementName == "item") || (self.rssType == "atom" && elementName == "entry")){
            var item = [String:String]()
            item["title"] = self.articleTitle
            item["link"] = self.articleLink
            self.result.append(item)
            self.isItemTag = false
            self.articleTitle = ""
            self.articleLink = ""
        }
        
        if(self.isItemTag && elementName == "title") {
            //print("----")
            isTitleTag = false
        }
        
        if(self.isItemTag && elementName == "link") {
            isLinkTag = false
        }
    }
    
    // XML解析終了時に実行されるメソッド
    func parserDidEndDocument(_ parser: XMLParser) {
        //print("XML解析終了しました")
        //print(self.result)
        self.articleTableView.reloadData()
    }
    
    // 解析中にエラーが発生した時に実行されるメソッド
    func parser(_ parser: XMLParser, parseErrorOccurred parseError: Error) {
        //print("エラー:" + parseError.localizedDescription)
    }

    
    // UIPickerViewの列の数
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    // UIPickerViewの行数、リストの数
    func pickerView(_ pickerView: UIPickerView,
                    numberOfRowsInComponent component: Int) -> Int {
        return rssUrlArray.count
    }
    
    // UIPickerViewの最初の表示
    func pickerView(_ pickerView: UIPickerView,
                    titleForRow row: Int,
                    forComponent component: Int) -> String? {
        
        var urlList: [String] = []
        
        for item in rssUrlArray {
            urlList.append(item.url)
        }
        
        return urlList[row]
    }
    
    // UIPickerViewのRowが選択された時の挙動
    func pickerView(_ pickerView: UIPickerView,
                    didSelectRow row: Int,
                    inComponent component: Int) {
        if(rssUrlArray.count > rssUrlPickerView.selectedRow(inComponent: 0)) {
            loadArticle(url: rssUrlArray[rssUrlPickerView.selectedRow(inComponent: 0)].url)
        } else {
            self.articleTableView.reloadData()
        }
    }
    
    // MARK: UITableViewDataSourceプロトコルのメソッド
    // データの数（＝セルの数）を返すメソッド
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.result.count
    }
    
    // 各セルの内容を返すメソッド
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        // 再利用可能な cell を得る
        let cell = articleTableView.dequeueReusableCell(withIdentifier: "articleCell", for: indexPath)
        
        let item = self.result[indexPath.row]
        cell.textLabel?.text = item["title"]
        cell.detailTextLabel?.text = item["link"]
        
        return cell
    }
    
    // MARK: UITableViewDelegateプロトコルのメソッド
    // 各セルを選択した時に実行されるメソッド
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        let item = self.result[indexPath.row]
        let url = URL(string: item["link"]!)
        UIApplication.shared.open(url!)
    }
}

