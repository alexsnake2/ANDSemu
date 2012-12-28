// PasswordDialog.cpp

#include "StdAfx.h"

// For compilers that support precompilation, includes "wx/wx.h".
#include "wx/wxprec.h"
 
#ifdef __BORLANDC__
    #pragma hdrstop
#endif

// for all others, include the necessary headers (this file is usually all you
// need because it includes almost all "standard" wxWidgets headers)
#ifndef WX_PRECOMP
    #include "wx/wx.h"
#endif  

#include "Windows/Control/DialogImpl.h"

#include "ListViewDialogRes.h"

class CListViewDialogImpl : public NWindows::NControl::CModalDialogImpl
{
  public:
   CListViewDialogImpl(NWindows::NControl::CModalDialog *dialog,wxWindow * parent,int id) :
	CModalDialogImpl(dialog,parent, id, wxT("ListView"), wxDefaultPosition, wxDefaultSize,
			   wxDEFAULT_DIALOG_STYLE | wxRESIZE_BORDER | wxMAXIMIZE_BOX | wxMINIMIZE_BOX)

  {

	wxBoxSizer* topsizer = new wxBoxSizer(wxVERTICAL);

	wxListCtrl *list = new wxListCtrl(this, IDC_LISTVIEW_LIST, wxDefaultPosition, wxSize(645,195), wxLC_REPORT | wxLC_NO_HEADER);

	topsizer->Add(list, 1, wxALL|wxEXPAND, 5);

	topsizer->Add(CreateButtonSizer(wxOK|wxCANCEL), 0, wxALL|wxEXPAND, 5);

	this->OnInit();

	SetSizer(topsizer); // use the sizer for layout
	topsizer->SetSizeHints(this); // set size hints to honour minimum size
  }
private:
	// Any class wishing to process wxWindows events must use this macro
	DECLARE_EVENT_TABLE()
};

REGISTER_DIALOG(IDD_DIALOG_LISTVIEW,CListViewDialog,0)

BEGIN_EVENT_TABLE(CListViewDialogImpl, wxDialog)
	EVT_BUTTON(wxID_ANY,   CModalDialogImpl::OnAnyButton)
	EVT_CHECKBOX(wxID_ANY, CModalDialogImpl::OnAnyButton)
END_EVENT_TABLE()

