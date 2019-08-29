//
//  RssUrl.swift
//  RssReaderForiOS
//
//  Created by stf01430 on 2019/07/09.
//  Copyright © 2019 tamfoi. All rights reserved.
//

import RealmSwift

class RssUrl: Object {
    //プライマリーキー
    @objc dynamic var id = 0
    
    //タイトル
    @objc dynamic var url = ""
    
    /**
     id をプライマリーキーとして設定
     */
    override static func primaryKey() -> String? {
        return "id"
    }
}
