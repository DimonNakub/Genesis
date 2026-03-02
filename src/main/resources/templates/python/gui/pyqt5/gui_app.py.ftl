"""
PyQt5 GUI Application for ${projectName}
Modern cross-platform GUI framework based on Qt
"""

import sys
import logging
from PyQt5.QtWidgets import (
    QApplication, QMainWindow, QWidget, QVBoxLayout, QHBoxLayout,
    QLabel, QLineEdit, QPushButton, QTextEdit, QGroupBox, QMessageBox
)
from PyQt5.QtCore import Qt

logger = logging.getLogger(__name__)


class ${projectName?replace("_", "")?cap_first}Window(QMainWindow):
    """Main application window"""

    def __init__(self):
        super().__init__()
        self.init_ui()
        logger.info("PyQt5 application initialized")

    def init_ui(self):
        """Initialize user interface"""
        self.setWindowTitle("${projectName}")
        self.setGeometry(100, 100, 800, 600)

        # Central widget
        central_widget = QWidget()
        self.setCentralWidget(central_widget)

        # Main layout
        layout = QVBoxLayout()
        central_widget.setLayout(layout)

        # Title
        title = QLabel("${projectName}")
        title.setAlignment(Qt.AlignCenter)
        title.setStyleSheet("font-size: 24px; font-weight: bold; padding: 20px;")
        layout.addWidget(title)

        # Input group
        input_group = QGroupBox("Input")
        input_layout = QVBoxLayout()
        input_group.setLayout(input_layout)

        # Name input
        name_layout = QHBoxLayout()
        name_layout.addWidget(QLabel("Name:"))
        self.name_input = QLineEdit()
        self.name_input.setPlaceholderText("Enter your name")
        name_layout.addWidget(self.name_input)
        input_layout.addLayout(name_layout)

        # Message input
        message_layout = QHBoxLayout()
        message_layout.addWidget(QLabel("Message:"))
        self.message_input = QLineEdit()
        self.message_input.setPlaceholderText("Enter a message")
        message_layout.addWidget(self.message_input)
        input_layout.addLayout(message_layout)

        layout.addWidget(input_group)

        # Buttons
        button_layout = QHBoxLayout()

        submit_btn = QPushButton("Submit")
        submit_btn.clicked.connect(self.on_submit)
        button_layout.addWidget(submit_btn)

        clear_btn = QPushButton("Clear")
        clear_btn.clicked.connect(self.on_clear)
        button_layout.addWidget(clear_btn)

        layout.addLayout(button_layout)

        # Output group
        output_group = QGroupBox("Output")
        output_layout = QVBoxLayout()
        output_group.setLayout(output_layout)

        self.output_text = QTextEdit()
        self.output_text.setReadOnly(True)
        output_layout.addWidget(self.output_text)

        layout.addWidget(output_group)

    def on_submit(self):
        """Handle submit button click"""
        name = self.name_input.text()
        message = self.message_input.text()

        if not name or not message:
            QMessageBox.warning(self, "Input Required", "Please fill in all fields")
            return

        # Process data here
        output = f"Name: {name}\nMessage: {message}\n"
        self.output_text.append(output)

        logger.info(f"Submitted: {name} - {message}")

    def on_clear(self):
        """Handle clear button click"""
        self.name_input.clear()
        self.message_input.clear()
        self.output_text.clear()


def main():
    """Main entry point for GUI application"""
    # Configure logging
    logging.basicConfig(level=logging.INFO)

    # Create application
    app = QApplication(sys.argv)

    # Create main window
    window = ${projectName?replace("_", "")?cap_first}Window()
    window.show()

    # Start event loop
    sys.exit(app.exec_())


if __name__ == "__main__":
    main()
