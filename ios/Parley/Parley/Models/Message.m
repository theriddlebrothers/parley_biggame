//
//  Message.m
//  Parley
//
//  Created by Joshua Riddle on 2/5/13.
//  Copyright (c) 2013 The Riddle Brothers. All rights reserved.
//

#import "Message.h"

@implementation Message

@synthesize message;
@synthesize authorName;
@synthesize dateCreated;
@synthesize profileUrl;

-(id)initWithDetails:(NSString *)inMessage authorName:(NSString *)inAuthorName dateCreated:(NSDate *)inDateCreated profileUrl:(NSString *)inProfileUrl {
    self = [super init];
    if (self) {
        self.message = inMessage;
        self.authorName = inAuthorName;
        self.dateCreated = inDateCreated;
        self.profileUrl = inProfileUrl;
    }
    return self;
}

@end
