
USE [eAdministration]
GO
/****** Object:  Table [dbo].[Account]    Script Date: 7/10/2014 3:28:15 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Account](
	[Username] [varchar](50) NOT NULL,
	[Password] [varchar](100) NOT NULL,
	[Role] [int] NOT NULL,
	[Name] [varchar](50) NOT NULL,
	[Birthday] [datetime] NOT NULL,
	[Email] [varchar](100) NOT NULL,
	[Phone] [varchar](20) NOT NULL,
	[DepartmentId] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[Username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Department]    Script Date: 7/10/2014 3:28:15 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Department](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Name] [varchar](50) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Lab]    Script Date: 7/10/2014 3:28:15 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Lab](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Name] [varchar](50) NOT NULL,
	[Type] [int] NOT NULL,
	[Status] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[LabSchedule]    Script Date: 7/10/2014 3:28:15 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[LabSchedule](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Slot] [int] NULL,
	[Date] [datetime] NULL,
	[Detail] [varchar](50) NULL,
	[LabId] [int] NULL,
	[RequestAccount] [varchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Request]    Script Date: 7/10/2014 3:28:15 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Request](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Content] [text] NOT NULL,
	[Status] [int] NULL,
	[RequestAccount] [varchar](50),
	[ResolveAccount] [varchar](50),
	[Type] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Resource]    Script Date: 7/10/2014 3:28:15 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Resource](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Type] [int] NULL,
	[Title] [varchar](300) NOT NULL,
	[Content] [text] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
INSERT [dbo].[Account] ([Username], [Password], [Role], [Name], [Birthday], [Email], [Phone], [DepartmentId]) VALUES (N'admin', N'admin', 1, N'A Nguyen', CAST(0x0000000000000000 AS DateTime), N'anguyen@LabAdministration.com', N'0901234567', NULL)
INSERT [dbo].[Account] ([Username], [Password], [Role], [Name], [Birthday], [Email], [Phone], [DepartmentId]) VALUES (N'bnguyen', N'hod', 4, N'B Nguyen', CAST(0x0000000000000000 AS DateTime), N'bnguyen@LabAdministration.com', N'0902358947', 1)
INSERT [dbo].[Account] ([Username], [Password], [Role], [Name], [Birthday], [Email], [Phone], [DepartmentId]) VALUES (N'cnguyen', N'technician', 3, N'C Nguyen', CAST(0x0000000000000000 AS DateTime), N'cnguyen@LabAdministration.com', N'0974583489', NULL)
INSERT [dbo].[Account] ([Username], [Password], [Role], [Name], [Birthday], [Email], [Phone], [DepartmentId]) VALUES (N'dnguyen', N'instructor', 2, N'D Nguyen', CAST(0x0000000000000000 AS DateTime), N'dnguyen@LabAdministration.com', N'0938956735', 1)
SET IDENTITY_INSERT [dbo].[Department] ON 

INSERT [dbo].[Department] ([Id], [Name]) VALUES (1, N'Economics')
INSERT [dbo].[Department] ([Id], [Name]) VALUES (2, N'Politics')
SET IDENTITY_INSERT [dbo].[Department] OFF
SET ANSI_PADDING ON

GO
/****** Object:  Index [UQ__Departme__737584F659365252]    Script Date: 7/10/2014 3:28:15 PM ******/
ALTER TABLE [dbo].[Department] ADD UNIQUE NONCLUSTERED 
(
	[Name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_PADDING ON

GO
/****** Object:  Index [UQ__Lab__737584F631706596]    Script Date: 7/10/2014 3:28:15 PM ******/
ALTER TABLE [dbo].[Lab] ADD UNIQUE NONCLUSTERED 
(
	[Name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_PADDING ON

GO
/****** Object:  Index [UQ__Resource__2CB664DCBD089BF4]    Script Date: 7/10/2014 3:28:15 PM ******/
ALTER TABLE [dbo].[Resource] ADD UNIQUE NONCLUSTERED 
(
	[Title] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Lab] ADD  CONSTRAINT [DF_Lab_Type]  DEFAULT ((1)) FOR [Type]
GO
ALTER TABLE [dbo].[Lab] ADD  CONSTRAINT [DF_Lab_Status]  DEFAULT ((1)) FOR [Status]
GO
ALTER TABLE [dbo].[Request] ADD  CONSTRAINT [DF_Request_Type]  DEFAULT ((1)) FOR [Type]
GO
ALTER TABLE [dbo].[Account]  WITH CHECK ADD FOREIGN KEY([DepartmentId])
REFERENCES [dbo].[Department] ([Id])
GO
ALTER TABLE [dbo].[LabSchedule]  WITH CHECK ADD FOREIGN KEY([LabId])
REFERENCES [dbo].[Lab] ([Id])
GO
ALTER TABLE [dbo].[LabSchedule]  WITH CHECK ADD FOREIGN KEY([RequestAccount])
REFERENCES [dbo].[Account] ([Username])
GO
ALTER TABLE [dbo].[Request]  WITH CHECK ADD FOREIGN KEY([RequestAccount])
REFERENCES [dbo].[Account] ([Username])
GO
ALTER TABLE [dbo].[Request]  WITH CHECK ADD FOREIGN KEY([ResolveAccount])
REFERENCES [dbo].[Account] ([Username])
GO
