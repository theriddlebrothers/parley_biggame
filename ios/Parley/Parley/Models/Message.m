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

-(id)initWithMessage:(NSString *)message {
    self = [super init];
    if (self) {
        self.message = message;
    }
    return self;
}

@end
