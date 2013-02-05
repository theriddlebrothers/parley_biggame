//
//  MessageTableViewCell.m
//  Parley
//
//  Created by Joshua Riddle on 2/5/13.
//  Copyright (c) 2013 The Riddle Brothers. All rights reserved.
//

#import "MessageTableViewCell.h"

@implementation MessageTableViewCell

@synthesize messageLabel;

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
