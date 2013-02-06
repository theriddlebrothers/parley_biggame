//
//  Message.h
//  Parley
//
//  Created by Joshua Riddle on 2/5/13.
//  Copyright (c) 2013 The Riddle Brothers. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Message : NSObject

@property (strong)NSString *message;
@property (strong)NSString *authorName;
@property (strong)NSDate *dateCreated;
@property (strong)NSString *profileUrl;


-(id)initWithDetails:(NSString *)message authorName:(NSString *)authorName dateCreated:(NSDate *)dateCreated profileUrl:(NSString *)profileUrl;

@end
