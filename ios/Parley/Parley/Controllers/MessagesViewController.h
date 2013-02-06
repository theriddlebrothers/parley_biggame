//
//  MessagesViewController.h
//  Parley
//
//  Created by Joshua Riddle on 2/5/13.
//  Copyright (c) 2013 The Riddle Brothers. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface MessagesViewController : UITableViewController

@property (strong)NSMutableArray *messages;
@property (nonatomic, strong) NSMutableData *responseData;

@end
